FROM opensearchstaging/opensearch:2.16.0

COPY ./build/distributions/opensearch-ubi-2.16.0.0.zip /tmp/

RUN /usr/share/opensearch/bin/opensearch-plugin install --batch file:/tmp/opensearch-ubi-2.16.0.0.zip
