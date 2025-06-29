#!/bin/bash

# Test the lunar calendar API with a date range that includes 2026-03-23
echo "Testing lunar calendar API with date range 2026-03-20 to 2026-03-25"

# First, login to get a session cookie
echo "Logging in to get a session..."
COOKIE_JAR="cookies.txt"
rm -f $COOKIE_JAR

curl -c $COOKIE_JAR -X POST "http://localhost:9090/login" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=admin"

# Now make the API call with the session cookie
echo -e "\nMaking API call with session cookie..."
curl -b $COOKIE_JAR -X POST "http://localhost:9090/api/calendar/make-calendar?startDate=2026-03-20&endDate=2026-03-25" \
  -H "Content-Type: application/json" \
  -v

# Clean up
rm -f $COOKIE_JAR

echo -e "\n\nTest completed."
