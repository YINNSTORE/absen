#!/bin/sh
set -eu

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION=8.7
CACHE_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/dists/manual-gradle-${GRADLE_VERSION}"
GRADLE_HOME="$CACHE_DIR/gradle-${GRADLE_VERSION}"

if [ -x "$GRADLE_HOME/bin/gradle" ]; then
    exec "$GRADLE_HOME/bin/gradle" "$@"
fi

if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
fi

ARCHIVE="$CACHE_DIR/gradle-${GRADLE_VERSION}-bin.zip"
mkdir -p "$CACHE_DIR"

echo "Gradle $GRADLE_VERSION belum tersedia. Mengunduh Gradle distribution..."

if command -v curl >/dev/null 2>&1; then
    curl -fL --retry 3 --connect-timeout 20 \
        "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
        -o "$ARCHIVE"
elif command -v wget >/dev/null 2>&1; then
    wget -O "$ARCHIVE" \
        "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
else
    echo "Butuh curl atau wget untuk mengunduh Gradle." >&2
    exit 1
fi

rm -rf "$GRADLE_HOME" "$CACHE_DIR/gradle-${GRADLE_VERSION}"
if command -v unzip >/dev/null 2>&1; then
    unzip -q "$ARCHIVE" -d "$CACHE_DIR"
else
    echo "Butuh unzip untuk mengekstrak Gradle." >&2
    exit 1
fi

exec "$GRADLE_HOME/bin/gradle" "$@"
