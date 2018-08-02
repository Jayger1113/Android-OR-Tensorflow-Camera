# Android-OR-Tensorflow-Camera
This project based on android tensorflow , retrain model to recognize NTD 1,5,10,50 coin

# Install tensorflow

1. $pip3 install tensorflow

2. $git clone https://github.com/tensorflow/tensorflow.git

3. $cd tensorflow

4. $git checkout r1.5


# retrain our model to learn from images

  1. gather lots of image (better larger than 20 images of each categories, or it might cause issue)

  2. retrain model by using retrain.py

    $cd /Users/garyhsu/workspace/git/tensorflow/tensorflow/examples/image_retraining

    $sudo python3 retrain.py --how_many_training_steps=4000 --model_dir=/Users/garyhsu/workspace/git/Android-OR-Tensorflow-Camera/tf_files --bottleneck_dir=/Users/garyhsu/workspace/git/Android-OR-Tensorflow-Camera/tf_files/bottlenecks --output_graph=/Users/garyhsu/workspace/git/Android-OR-Tensorflow-Camera/tf_files/retrained_graph.pb --output_labels=/Users/garyhsu/workspace/git/Android-OR-Tensorflow-Camera/tf_files/retrained_labels.txt --image_dir=/Users/garyhsu/workspace/git/Android-OR-Tensorflow-Camera/coin

# Optimize the model

    $cd /Users/garyhsu/workspace/git/tensorflow/tensorflow/python/tools
    
    $python3 optimize_for_inference.py --input=/Users/garyhsu/tf_files/retrained_graph.pb --output=/Users/garyhsu/tf_files/optimized_graph.pb --input_names="Mul" --output_names="final_result"


# Demo
https://youtu.be/i1bBcNKei8c


# Reference
https://codelabs.developers.google.com/codelabs/tensorflow-for-poets/#0


# 2018/08/01 new feature release(upload image to google drive)

# prerequire: 
1. google account login
2. go to google API console
3. follow the tutorial (https://support.google.com/googleapi/answer/6158849?hl=zh-Hant#installedapplications&android)
4. google api offial website (https://developers.google.com/drive/api/v3/about-sdk)




