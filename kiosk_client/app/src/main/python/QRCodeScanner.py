import cv2

# Create a VideoCapture object to access the default camera
cap = cv2.VideoCapture(0)

# Check if camera was successfully opened
if not cap.isOpened():
    print("Error: Could not open camera.")
    exit()

# Create a window to display the camera stream
cv2.namedWindow("Webcam Stream")

# Loop to capture frames from the camera and display them in the window
while True:
    # Read a frame from the camera
    ret, frame = cap.read()

    # Check if frame was successfully read
    if not ret:
        print("Error: Could not read frame.")
        break

    # Display the frame in
