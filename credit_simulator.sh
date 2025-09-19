#!/bin/bash

# ============================================================================
# Credit Simulator - Simple & Clean Launcher
# ============================================================================
# Purpose: Backend Engineer Test - Clean Maven Build & Run Script
# Requirements: Java 17+, Maven
# ============================================================================

# Basic color codes for better UX
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RESET='\033[0m'

# Navigate to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Configuration
APP_NAME="Credit Simulator"
JAR_PATTERN="target/credit_simulator-*.jar"

# Parse command line arguments
CLEAN_BUILD=false
RUN_TESTS=false
INPUT_FILE=""

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        --clean)
            CLEAN_BUILD=true
            shift
            ;;
        --test)
            RUN_TESTS=true
            shift
            ;;
        -*)
            log_error "Unknown option: $1"
            show_help
            exit 1
            ;;
        *)
            INPUT_FILE="$1"
            shift
            ;;
    esac
done

# ============================================================================
# Helper Functions
# ============================================================================

log_info() {
    echo -e "${BLUE}[INFO]${RESET} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${RESET} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${RESET} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${RESET} $1"
}

print_banner() {
    echo ""
    echo -e "${BLUE}================================================================${RESET}"
    echo -e "${BLUE}             $APP_NAME - Backend Engineer Test${RESET}"
    echo -e "${BLUE}================================================================${RESET}"
    echo ""
}

show_help() {
    echo "Usage: ./credit_simulator [OPTIONS] [INPUT_FILE]"
    echo ""
    echo "OPTIONS:"
    echo "  -h, --help     Show this help message"
    echo "  --clean        Clean build (remove target directory)"
    echo "  --test         Run unit tests"
    echo ""
    echo "EXAMPLES:"
    echo "  ./credit_simulator                    # Interactive mode"
    echo "  ./credit_simulator file_inputs.txt   # File input mode"
    echo "  ./credit_simulator --clean           # Clean build first"
    echo "  ./credit_simulator --test            # Run with tests"
    echo ""
}

# ============================================================================
# System Checks
# ============================================================================

check_java() {
    if ! command -v java &> /dev/null; then
        log_error "Java is not installed or not in PATH"
        echo "Please install Java 17 or higher from: https://adoptium.net/"
        return 1
    fi

    # Simple Java version display
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    log_success "Java found: $java_version"
    return 0
}

check_maven() {
    # Check if Maven wrapper exists first (preferred)
    if [[ -f "mvnw" ]]; then
        log_success "Maven wrapper found: mvnw"
        MAVEN_CMD="./mvnw"
        return 0
    fi

    # Fall back to system Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven is not installed and no Maven wrapper found"
        echo "Please install Maven from: https://maven.apache.org/download.cgi"
        return 1
    fi

    maven_version=$(mvn -version 2>&1 | head -n 1 | awk '{print $3}')
    log_success "System Maven found: $maven_version"
    MAVEN_CMD="mvn"
    return 0
}

# ============================================================================
# Build Functions
# ============================================================================

clean_build() {
    log_info "Cleaning previous build artifacts..."
    $MAVEN_CMD clean -q
    if [[ $? -eq 0 ]]; then
        log_success "Clean completed"
    else
        log_error "Clean failed"
        return 1
    fi
}

build_application() {
    log_info "Building application..."
    log_info "Command: $MAVEN_CMD clean compile package -DskipTests"

    $MAVEN_CMD clean compile package -DskipTests -q

    if [[ $? -eq 0 ]]; then
        log_success "Build completed successfully"
        return 0
    else
        log_error "Build failed. Running with verbose output..."
        echo ""
        $MAVEN_CMD clean compile package -DskipTests
        return 1
    fi
}

run_tests() {
    log_info "Running unit tests..."
    $MAVEN_CMD test -q

    if [[ $? -eq 0 ]]; then
        log_success "All tests passed"
        return 0
    else
        log_error "Tests failed"
        return 1
    fi
}

find_jar() {
    JAR_FILE=""

    # Find the executable JAR (exclude original)
    for jar_file in $JAR_PATTERN; do
        if [[ -f "$jar_file" ]]; then
            filename=$(basename "$jar_file" .jar)
            if [[ "$filename" != *"original"* ]]; then
                JAR_FILE="$jar_file"
                log_success "Found JAR: $JAR_FILE"
                return 0
            fi
        fi
    done

    log_error "No executable JAR found"
    echo "Expected pattern: $JAR_PATTERN"
    return 1
}

# ============================================================================
# Application Execution
# ============================================================================

run_application() {
    local input_file="$1"

    if [[ -z "$input_file" ]]; then
        log_info "Starting in interactive mode..."
        echo ""
        echo -e "${BLUE}================================================================${RESET}"
        java -jar "$JAR_FILE"
    else
        if [[ ! -f "$input_file" ]]; then
            log_error "Input file not found: $input_file"
            return 1
        fi

        log_info "Processing input file: $input_file"
        echo ""
        echo -e "${BLUE}================================================================${RESET}"
        java -jar "$JAR_FILE" "$input_file"
    fi

    local APP_EXIT_CODE=$?
    echo ""
    echo -e "${BLUE}================================================================${RESET}"

    if [[ $APP_EXIT_CODE -eq 0 ]]; then
        log_success "Application completed successfully"
    else
        log_error "Application failed with exit code: $APP_EXIT_CODE"
    fi

    return $APP_EXIT_CODE
}

# ============================================================================
# Main Execution
# ============================================================================

print_banner

# System checks
check_java
if [[ $? -ne 0 ]]; then
    exit 1
fi

check_maven
if [[ $? -ne 0 ]]; then
    exit 1
fi

echo ""

# Build process
if [[ "$CLEAN_BUILD" == "true" ]]; then
    clean_build
    if [[ $? -ne 0 ]]; then
        exit 1
    fi
fi

build_application
if [[ $? -ne 0 ]]; then
    exit 1
fi

echo ""

# Testing (if requested)
if [[ "$RUN_TESTS" == "true" ]]; then
    run_tests
    if [[ $? -ne 0 ]]; then
        exit 1
    fi
    echo ""
fi

# Find and run application
find_jar
if [[ $? -ne 0 ]]; then
    exit 1
fi

echo ""

run_application "$INPUT_FILE"
exit $?
