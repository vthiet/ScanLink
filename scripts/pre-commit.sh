#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status, but we want to capture errors manually
set -o pipefail

# ANSI color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper output functions
info() { echo -e "${BLUE}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }
success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }

# ------------------------------------------------------------------------------
# Hook Installer
# ------------------------------------------------------------------------------
if [[ "$1" == "--install" || "$1" == "-i" ]]; then
    # Resolve the git hook directory
    HOOK_DIR=$(git rev-parse --git-path hooks 2>/dev/null)
    if [ -z "$HOOK_DIR" ]; then
        error "Not a git repository or git is not installed."
        exit 1
    fi
    
    HOOK_FILE="$HOOK_DIR/pre-commit"
    info "Installing pre-commit hook..."
    
    # Copy this script to .git/hooks/pre-commit
    cp "$0" "$HOOK_FILE"
    chmod +x "$HOOK_FILE"
    
    success "Pre-commit hook successfully installed at: $HOOK_FILE"
    exit 0
fi

# ------------------------------------------------------------------------------
# Check Staged Changes
# ------------------------------------------------------------------------------
STAGED_FILES=$(git diff --cached --name-only --diff-filter=d)

if [ -z "$STAGED_FILES" ]; then
    info "No staged changes to verify."
    exit 0
fi

# ------------------------------------------------------------------------------
# 1. Scan for Forbidden Files
# ------------------------------------------------------------------------------
info "Checking for forbidden files in staged changes..."
forbidden_found=0

for file in $STAGED_FILES; do
    basename=$(basename "$file")
    ext="${file##*.}"
    
    # Exact name check
    if [[ "$basename" == "local.properties" || \
          "$basename" == "google-services.json" || \
          "$basename" == "scanlink-firebase-adminsdk.json" || \
          "$basename" == "id_rsa" || \
          "$basename" == "id_dsa" ]]; then
        error "Forbidden sensitive file staged for commit: $file"
        forbidden_found=1
    fi
    
    # Extension check
    if [[ "$ext" == "jks" || "$ext" == "keystore" || "$ext" == "pem" || "$ext" == "p12" || "$ext" == "pkcs12" ]]; then
        error "Forbidden keystore or private key file staged for commit: $file"
        forbidden_found=1
    fi
done

if [ $forbidden_found -ne 0 ]; then
    error "Commit aborted. Please remove sensitive files from staged changes."
    exit 1
fi
success "No forbidden files found in staging."

# ------------------------------------------------------------------------------
# 2. Scan for Secrets in Staged Diffs
# ------------------------------------------------------------------------------
info "Scanning staged diffs for secrets and keys..."
secrets_found=0

for file in $STAGED_FILES; do
    # Skip if file does not exist (e.g. deleted)
    [ -f "$file" ] || continue
    
    # Skip binary files or common image/asset formats to avoid false positives and speed up execution
    if [[ "$file" =~ \.(png|jpg|jpeg|gif|webp|svg|ico|pdf|zip|gz|tar|mp3|mp4|aar|apk|jar|jks|keystore)$ ]]; then
        continue
    fi
    
    # Extract only the added lines in this commit
    diff_added=$(git diff --cached -U0 "$file" | grep '^[+][^+]' | cut -c 2-)
    
    if [ -n "$diff_added" ]; then
        # A. Google / Firebase API Key Check
        if echo "$diff_added" | grep -q -P 'AIza[0-9A-Za-z-_]{35}'; then
            error "Google/Firebase API Key detected in $file:"
            echo "$diff_added" | grep -P --color=always 'AIza[0-9A-Za-z-_]{35}'
            secrets_found=1
        fi
        
        # B. Private Key Check
        if echo "$diff_added" | grep -q -P -- '-----BEGIN( [A-Z0-9_]+)? PRIVATE KEY-----'; then
            error "Private Key structure detected in $file:"
            echo "$diff_added" | grep -P --color=always -- '-----BEGIN( [A-Z0-9_]+)? PRIVATE KEY-----'
            secrets_found=1
        fi
        
        # C. Generic Credentials Pattern (key/secret/password/token/credential/jwt/auth matching assignment)
        generic_regex='(?i)\b(api_?key|secret|password|token|credential|auth_?token|client_?secret|jwt)\b\s*[:=]\s*["''][a-zA-Z0-9_\-\.\~\+\/]{16,}["'']'
        if echo "$diff_added" | grep -q -P "$generic_regex"; then
            error "Potential hardcoded secret/credentials detected in $file:"
            echo "$diff_added" | grep -P --color=always "$generic_regex"
            secrets_found=1
        fi
        
        # D. Slack / Discord Webhooks Check
        webhook_regex='https://(hooks\.slack\.com/services/T[A-Z0-9]+/B[A-Z0-9]+/[A-Z0-9]+|discord\.com/api/webhooks/[0-9]+/[A-Za-z0-9_-]+)'
        if echo "$diff_added" | grep -q -P "$webhook_regex"; then
            error "Webhook URL detected in $file:"
            echo "$diff_added" | grep -P --color=always "$webhook_regex"
            secrets_found=1
        fi
    fi
done

if [ $secrets_found -ne 0 ]; then
    error "Commit aborted. Please remove exposed secrets or use 'git commit --no-verify' to bypass."
    exit 1
fi
success "No exposed secrets or credentials detected."

# ------------------------------------------------------------------------------
# 3. Smart Gradle Build Validation
# ------------------------------------------------------------------------------
if [ "$SKIP_GRADLE" = "true" ]; then
    warn "SKIP_GRADLE is set to true. Skipping Gradle build and test checks."
    exit 0
fi

# Determine if Gradle build-related files are staged
build_needed=0
for file in $STAGED_FILES; do
    if [[ "$file" =~ \.(kt|kts|java|xml|gradle|properties)$ || "$(basename "$file")" == "gradlew" ]]; then
        build_needed=1
        break
    fi
done

if [ $build_needed -eq 0 ]; then
    info "No build-related files modified (Kotlin, Java, XML, Gradle). Skipping Gradle build checks."
    exit 0
fi

# Ensure Gradle wrapper is executable
if [ ! -x "./gradlew" ]; then
    if [ -f "./gradlew" ]; then
        info "Making gradlew wrapper executable..."
        chmod +x ./gradlew
    else
        error "gradlew wrapper not found in root directory. Cannot perform build check."
        exit 1
    fi
fi

# Run Gradle checks
info "Starting Gradle build validation checks..."

# A. Compile check
info "Running code compilation (compileDebugSources compileReleaseSources)..."
if ! ./gradlew compileDebugSources compileReleaseSources; then
    error "Gradle compilation failed. Please fix syntax/compilation issues before committing."
    exit 1
fi
success "Compilation checks passed."

# B. Unit Tests
info "Running unit tests (testDebugUnitTest)..."
if ! ./gradlew testDebugUnitTest; then
    error "Unit tests failed. Please resolve test failures before committing."
    exit 1
fi
success "Unit tests passed."

# C. Android Lint
info "Running Android static analysis (lintDebug)..."
if ! ./gradlew lintDebug; then
    error "Lint check failed. Please resolve lint warnings/errors before committing."
    exit 1
fi
success "Lint check passed."

success "All pre-commit verification checks completed successfully!"
exit 0
