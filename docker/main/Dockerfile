ARG OPENJDK_17_RUNTIME_IMAGE
FROM ${OPENJDK_17_RUNTIME_IMAGE}:0.2.1 as runtime

COPY --chown=default:default src/target/*.jar /home/default/app.jar

ENV LANG=en_US.UTF-8

COPY --chown=default:default docker/main/koolbox*.yaml /var/lib/app/

COPY --chown=default:default src/src/main/resources/migrations /home/default/migrations

COPY --chown=default:default docker/main/docker-entrypoint.sh /home/default/docker-entrypoint.sh
ENTRYPOINT ["./docker-entrypoint.sh"]
CMD ["run"]

EXPOSE 8080
EXPOSE 1514
