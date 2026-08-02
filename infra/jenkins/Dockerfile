FROM docker:28-cli AS docker-cli

FROM jenkins/jenkins:lts-jdk21

USER root
RUN apt-get update \
    && apt-get install --yes --no-install-recommends ansible-core maven \
    && rm -rf /var/lib/apt/lists/*
COPY --from=docker-cli /usr/local/bin/docker /usr/local/bin/docker
COPY --from=docker-cli /usr/local/libexec/docker/cli-plugins /usr/local/libexec/docker/cli-plugins

USER jenkins
