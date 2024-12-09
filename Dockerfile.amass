# Use the official caffix/amass image as the base
FROM caffix/amass:latest

# Set the working directory inside the container
WORKDIR /opt/amass

# Expose a volume for output storage (optional)
VOLUME ["/opt/amass/output"]

# Expose ports if Amass needs external access (e.g., APIs)
EXPOSE 8080

# Default entrypoint to keep the container running or handle commands
ENTRYPOINT ["/bin/sh", "-c", "tail -f /dev/null"]
