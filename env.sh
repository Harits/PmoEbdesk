#!/bin/sh
cat <<JSON > /usr/share/nginx/html/config.json
{
  "OPENPROJECT_URL": "${OPENPROJECT_URL}",
  "OPENPROJECT_API_KEY": "${OPENPROJECT_API_KEY}",
  "USE_MOCK_DATA": ${USE_MOCK_DATA:-false}
}
JSON
