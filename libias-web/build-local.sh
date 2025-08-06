#/bin/bash

BASE=/
rm -rf dist/*
cp ./config/env.config.local.ts src/client/app/shared/config/env.config.ts
npm run build.dev -- --base=$BASE
