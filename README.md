# Bucket-Fill
A scaled-down paint utility with an implementation of the bucket fill tool.

## The Filler Algorithm

This algorithm uses Breadth-First Traversal (BFT) to fill any irregular shapes.

## The rest of the tools

There are drawing and erasing tools with varying brush sizes. The canvas is a compressed 2D array by a factor of 6. If the canvas is not compressed and left with its pixels, the program would be excruciatingly slow (and sometimes crashes).
