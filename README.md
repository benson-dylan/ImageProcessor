<h1>Parallel Image Processor</h1>
<p>Our project sought to create an application in Java that processes images with parallelized algorithms to increase efficiency</p>

<h2>Compilation Instructions</h2>
<p>Using IntelliJ by JetBrains the App.java file can be run to test our program. <br> Alternatively we have provided a JAR file so the code can be executed on any device with a Java Dev Kit installed on it.</p>
<a href="https://drive.google.com/file/d/1gIt5Cb67hymGHnoERkhxpoQZxkfX-Wdz/view?usp=sharing">Download</a>

<h2>Goals, Challenges, Accomplishments</h2>
<p>The goal of our application was to achieve basic image processing / computer vision functions including but not limited to blur, sharpen, resizing, etc. At the moment we have achieved functions such as zoom, blur, and edge detection with more in progress. The biggest challenge to our development is understanding the processing algorithms. Java gives us a lot of tools to work with, but in order to parallelize the functions, we have to ignore some of the tools given to us to break down the algorithms. Our goal is to get a broad array of functions to display various and distinct features.</p><br>

<h2>Rough Draft/Outline</h2>
<h3>Title</h3>
<p>Parallel Image Processing: An Efficient Method to Basic Computer Vision</p>
<h3>Abstract</h3>
 <ul>
   <li><p>Introduction: Image processing involves image input and the manipulation of pixels to achieve a desired outcome.</p></li>
   <li><p>Objective: Our project seeks a method that optimizes these processes through multithreading.</p></li>
   <li><p>Methodology: We will be taking standard level image processing functions and utilizing Java's executor service to parallelize these features. Discuss the form of parallelization we chose (pixel by pixel, row by row, split into regions, etc.)</p></li>
   <li><p>Results: Through our methods we have created a more streamlined approach to image processing with equal accuracy to the original algorithms.</p></li>
   <li><p>Conclusion: With further expansion to out application, we could include more complex features that may have lengthier runtimes that we could improve through our methods.</p></li>
 </ul>
<h3>Introduction</h3>
 <ul>
  <li><p>Background: Image processing is an important part of not only media production but also computer vision.</p></li>
  <li><p>Structure: We will be discussing the algorithms we used for each feature and the form of parallelization we used to optimize said features.</p></li>
  <li><p>Hypothesis: We believe with proper load balancing and mutual exclusion we can achieve a more efficient image processor with high accuracy and no image corruption.</p></li>
 </ul>
<h3>Problem Statement</h3>
 <p>We were seeking a solution to potential runtime slowdowns due to complex algorithms running through high resolution images.</p> <br>
 <p>We are limiting our research to a specific set of features and algorithms to establish a rudimentary basis on what can be achieved through our methods.</p>
<h3>Related Work</h3>
 <p>Discuss articles and other sources we referenced in researching features</p>
 <p>Discuss connection to our problem</p>
<h3>Technique/Methods</h3>
 <ul>
  <li><p>JavaFX: We used JavaFX as the framework of our application which provides libraries to display images but conversion to Java Buffered Images was required to process pixels.</p></li>
  <li><p>Java AWT Buffered Image: Buffered Image gives us access to every index and an image's pixel array, allowing us to easily process images.</p></li>
  <li>
   <p>Features</p>
   <ul>
    <li><p>Blur: Described as Gaussian blur, uses a blur kernel to average neighboring pixel color values to "muddy" images. Discuss the blur algorithm</p></li>
    <li><p>Sharpen: Discuss sharpen algorithm</p></li>
    <li><p>Zoom: Described as the process of increasing pixel sizes based on a new input ratio, preserves aspect ratio. Discuss zoom algorithm</p></li>
    <li><p>Resize: Described as changing the dimensions of an image, potentially does not preserve aspect ratio. Discuss resize algorithm.</p></li>
    <li><p>Find Contours: Discuss contour algorithm</p></li>
    <li><p>Edge Detection: Described as a multi-step process to find the "strong edges of an image". Steps include Greyscaling, Gaussian Blur, Non-Maximum Suppression, Double Threshold, and finally Edge Detection through Hysteresis. Discuss edge detection algorithm.</p></li>
   </ul>
  </li>
  <li>
   <p>Multithreading</p>
   <ul>
    <li><p>Pixel by Pixel</p></li>
    <li><p>Line by Line</p></li>
    <li><p>Split into Regions</p></li>
   </ul>
  </li>
 </ul>
<h3>Evaluation</h3>
<ul>
 <li><p>Discuss time differences even if insignificant.</p></li>
 <li><p>Discuss samples and resolutions.</p></li>
 <li><p>Discuss visual accuracy of the outputs at all steps.</p></li>
</ul>
<h3>Discussion</h3>
 <ul>
  <li><p>Summarize findings</p></li>
  <li><p>Compare with research</p></li>
  <li><p>Recommendations for further research</p></li>
 </ul>
<h3>Conclusion</h3>
 <ul>
  
 </ul>

 
