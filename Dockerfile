FROM opensearchstaging/opensearch:2.19.0

ARG UBI_VERSION="2.19.0.0"

COPY ./build/distributions/opensearch-ubi-${UBI_VERSION}.zip /tmp/

# Required for OTel capabilities.
#RUN /usr/share/opensearch/bin/opensearch-plugin install --batch telemetry-otel

RUN /usr/share/opensearch/bin/opensearch-plugin install --batch file:/tmp/opensearch-ubi-${UBI_VERSION}.zip