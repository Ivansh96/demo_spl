#!/usr/bin/env bash

set -e

case $1 in
  run)
    koolbox run --config /var/lib/app/koolbox.yaml -vvv
    ;;
  init)
    koolbox run --config /var/lib/app/koolbox.init.yaml -vvv
    ;;
  migrations)
    koolbox run --config /var/lib/app/koolbox.migrations.yaml -vvv
    ;;
  *)
    exec "$@"
    ;;
esac
