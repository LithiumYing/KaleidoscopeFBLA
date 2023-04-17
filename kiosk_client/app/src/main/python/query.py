# THIS IS GOING BYE BYE ONCE ETHAN HAS TIME

import requests
import json

# import pandas as pd

with open("kiosk_client/app/src/main/resources/studentId.txt", "r") as file:
    studentId = file.read()

query = """
query {
  studentById(studentId: %s) {
    firstName
    lastName
  }
}""" % str(
    studentId
)

access_token = "your_access_token"
headers = {
    "Accept": "application/json",
    "Authorization": "JWT eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InNlMjYwOTVAc3R1ZGVudHMubWNwYXNkLmsxMi53aS51cyIsImV4cCI6MTY4MDcxODc5NCwib3JpZ0lhdCI6MTY4MDI4Njc5NH0.luMN5h1pAKcGpVRTUoJsAzrThfiCh0Kpr8630pvF7Zw",
    "Content-Type": "application/json",
}
url = "https://kaleidoscope-fbla.herokuapp.com/graphql/"
r = requests.get(url, json={"query": query}, headers=headers)
response = json.loads(r.text)
print(response)

with open("kiosk_client/app/src/main/resources/status.txt", "w") as f:
    f.write(str(r.status_code))

with open("kiosk_client/app/src/main/resources/name.txt", "w") as f:
    if "errors" in response:
        f.write("error")
    else:
        f.write(
            response["data"]["studentById"]["firstName"]
            + " "
            + response["data"]["studentById"]["lastName"]
        )
