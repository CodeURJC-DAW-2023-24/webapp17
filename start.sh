#!/bin/bash

# 1. Start 'ollama serve' with the environment variable OLLAMA_HOST=0.0.0.0 in the background
echo "Starting ollama serve..."
OLLAMA_HOST=0.0.0.0 ollama serve &

# Save the process ID of 'ollama serve' (optional, in case you want to control it later)
OLLAMA_PID=$!

# 2. Check if the Docker network 'app-network' exists; if not, create it
if ! docker network ls | grep -q "app-network"; then
  echo "Creating Docker network 'app-network'..."
  docker network create app-network
else
  echo "Docker network 'app-network' already exists."
fi

# 3. Run 'docker compose up --build' to build and start the containers
echo "Running 'docker compose up --build'..."
docker compose up --build

# Optional: Wait for the 'ollama serve' process to finish if needed
# wait $OLLAMA_PID
