FROM opensearchproject/opensearch:2.14.0

COPY ./build/distributions/opensearch-ubi-0.0.12.1-os2.14.0.zip /tmp/

RUN /usr/share/opensearch/bin/opensearch-plugin install --batch file:/tmp/opensearch-ubi-0.0.12.1-os2.14.0.zip
