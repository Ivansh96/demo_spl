#!/usr/bin/env bash

set -eou pipefail

build-docker-image-path() {
  printf "%s" "$ARTIFACT_NAMESPACE/$APP_NAME"
}

build-docker-image-full-path() {
  printf "%s" "$REGISTRY/$(build-docker-image-path):$APP_VERSION"
}

get-helm-chart-version() {
  cat helm/charts/main/Chart.yaml | sed -n 's/^version:\s*\(.*\)$/\1/p' | tr -d '\n'
}

get-helm-chart-name() {
  cat helm/charts/main/Chart.yaml | sed -n 's/^name:\s*\(.*\)$/\1/p' | tr -d '\n'
}

build-maven-project() {
  local SKIP_TESTS="$1"
  cd "$SOURCES_DIR"
  mvn -B -T 1C -s "$SETTINGS_XML_PATH" -DskipTests="$SKIP_TESTS" clean package
}

build-docker-image() {
  local IMAGE_FULL_PATH="$(build-docker-image-full-path)"

  docker login "$REGISTRY" -u "$USERNAME" -p "$PASSWORD"

  # shellcheck disable=SC2068
  docker build \
    --target=runtime \
    --build-arg OPENJDK_17_RUNTIME_IMAGE="$OPENJDK_17_RUNTIME_IMAGE"\
    -t "$IMAGE_FULL_PATH" \
    -f "docker/main/Dockerfile" \
    .

    docker push "$IMAGE_FULL_PATH"
}

publish-helm-chart() {
  local HELM_CHART_FILENAME="$CHART_NAME-$CHART_VERSION.tgz" # Build chart filename

  # Set Docker images for current environment
  yq -i ".main.image.registry |= \"$REGISTRY\"" helm/charts/main/values.yaml # Replace docker image registry for current environment
  yq -i ".main.image.path |= \"$ARTIFACT_NAMESPACE/$APP_NAME\"" helm/charts/main/values.yaml # Replace docker image path for current environment
  yq -i ".main.image.tag |= \"$APP_VERSION\"" helm/charts/main/values.yaml # Replace docker image tag for current environment

  # Publish the Chart
  helm package helm/charts/main --version "$CHART_VERSION" --app-version "$APP_VERSION" # Create helm chart package
  curl -s -u "$USERNAME:$PASSWORD" --insecure "$HELM_REGISTRY/" --upload-file "$HELM_CHART_FILENAME" # Upload helm chart
}

"${@:-unknown_command}"
