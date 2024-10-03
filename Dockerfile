FROM opensearchstaging/opensearch:3.0.0

ARG UBI_VERSION="3.0.0.0-SNAPSHOT"

COPY ./build/distributions/opensearch-ubi-${UBI_VERSION}.zip /tmp/

RUN /usr/share/opensearch/bin/opensearch-plugin install --batch file:/tmp/opensearch-ubi-${UBI_VERSION}.zip
