FROM opensearchproject/opensearch:3.1.0

ARG UBI_VERSION="3.1.0.0-SNAPSHOT"

COPY ./build/distributions/opensearch-ubi-${UBI_VERSION}.zip /tmp/

# Required for OTel capabilities.
#RUN /usr/share/opensearch/bin/opensearch-plugin install --batch telemetry-otel

RUN /usr/share/opensearch/bin/opensearch-plugin remove opensearch-ubi || true && /usr/share/opensearch/bin/opensearch-plugin install --batch file:/tmp/opensearch-ubi-${UBI_VERSION}.zip
