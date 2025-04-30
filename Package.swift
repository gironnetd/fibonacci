// swift-tools-version:5.3
import PackageDescription

let package = Package(
   name: "Fibonacci",
   platforms: [
     .iOS(.v14),
   ],
   products: [
      .library(name: "Fibonacci", targets: ["Fibonacci"])
   ],
   targets: [
      .binaryTarget(
         name: "Fibonacci",
         url: "https://github.com/gironnetd/fibonacci/releases/download/1.0.12/Fibonacci.xcframework.zip",
         checksum:"cbb0deb4894269da320acc489c745f08a29de5565a53cde6e52c49ae4e1e72c8")
   ]
)