Hi Viewers

This is my github repo and if you want to run it on your local device follow these simple steps -> clone this file in your local and setup your intellij -> simply kill the PID on port 8080 if exists and then run it locally ->hit the curls atach via postman to find the output

Test Steps

Upload PDF → Get fileId
Search using fileId and send extra param useSemantic as true for ai search
Try one-shot search and send extra param useSemantic as true for ai search
Work flow

Upload API: PDF → Extract text by pages → Store in memory with UUID
Search API: Find text matches → Create snippets → Calculate scores → Sort results
Temp Storage : In-memory HashMap (UUID → DocumentData)->Concurrent hashmap is used


API COLLECTION

https://swxedcrftgbyhj.postman.co/workspace/swxedcrftgbyhj-Workspace~fb9381a2-2574-4d10-899f-0e7b0dd9013c/collection/32656590-34e54ae3-1fb3-4486-89c6-3e49aae73844?action=share&source=copy-link&creator=32656590
