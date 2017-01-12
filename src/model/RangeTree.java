package model;

import java.util.ArrayList;

public class RangeTree {
    private class Node {
        private Point point;
        private int depth = 0;
        private int numberOfLeaves = 1;
        private Node predecessor;
        private Node left;
        private Node right;

        // Constructors
        public Node(Point aPoint, Node aPredecessor) {
            point = new Point(aPoint);
            predecessor = aPredecessor;
        }

        /* Util
        // add every node of a (sub)tree to a given tree
        public void join(Node treeToJoin) {
            if(!isLeaf()) {
                left.join(treeToJoin);
                right.join(treeToJoin);
            } else
                treeToJoin.add(value);
        }*/
        
        public void add(Point aPoint) {
            if (!isLeaf()) {
                if (aPoint.value <= point.value)
                    left.add(aPoint);
                else
                    right.add(aPoint);
            } else {
                if (aPoint.value < point.value) {
                    left = new Node(aPoint, this);
                    right = new Node(point, this);
                    point.value = aPoint.value;
                } else {
                    left = new Node(point, this);
                    right = new Node(aPoint, this);
                }
                updateDepth();
            }
            numberOfLeaves++;
            point.weight = Math.max(point.weight, aPoint.weight);
        }
        
        private void reportSubtree(ArrayList<Point> list) {
            if (!isLeaf()) {
                left.reportSubtree(list);
                right.reportSubtree(list);
            } else {
                list.add(point);
            }
        }
        
        /*
        private void balance() {
            int depthDifference = left.getDepth() - right.getDepth();
            Node removedTree = null;
            Node remainingTree = null;
            if (depthDifference == 2) { // left tree is too deep
                if (predecessor != null) {
                    if(predecessor.left == this)
                        predecessor.left = left;
                    else
                        predecessor.right = left;
                    predecessor.updateDepth();
                } else
                    parent.setRoot(left);
                left.predecessor = predecessor;
                removedTree = right;
                System.out.println("Removed tree is " + removedTree.value);
                remainingTree = left;
            } else if (depthDifference == -2) { // right tree is to deep
                if (predecessor != null){
                    if(predecessor.left == this)
                        predecessor.left = right;
                    else
                        predecessor.right = right;
                    predecessor.updateDepth();
                } else
                    parent.setRoot(right);
                right.predecessor = predecessor;
                removedTree = left;
                System.out.println("Removed tree is " + removedTree.value);
                remainingTree = right;
            } else {
                if (predecessor != null)
                    predecessor.balance();
            }
            
            if (removedTree != null) // fix tree
                removedTree.join(remainingTree);
        }
        */

        public boolean isLeaf() {
            return (left == null && right == null);
        }

        public void printTree() {
            if (!isLeaf()) {
                //System.out.println("L" + depth + ": " + point.value + " weight is " + point.weight);
                left.printTree();
                right.printTree();
            } else {
                System.out.println("Leaf: " + point.value + " weight is " + point.weight);

            }
        }
        
        public void updateDepth() {
            int expectedDepth = Math.max(left.depth, right.depth + 1);
            if (depth != Math.max(left.depth, right.depth + 1)) {
                depth = expectedDepth;
                if (predecessor != null)
                    predecessor.updateDepth();
            }
        }
    }

    private Node root;

    public RangeTree(Point[] valuesToInsert) {
        mergeSort(valuesToInsert);
        long start = System.currentTimeMillis();
        add(valuesToInsert);
        System.out.println("tree constrcted in " + (System.currentTimeMillis() - start));
    }
    
    private void add(Point[] valuesToInsert) {
        int nbOfValues = valuesToInsert.length;
        if(nbOfValues > 1) {
            add(valuesToInsert[(nbOfValues - 1)/2]);

            Point[] left = new Point[nbOfValues - nbOfValues/2 - 1];
            for (int i = 0; i < left.length; i++)
                left[i] = valuesToInsert[i];
            Point[] right = new Point[nbOfValues - left.length - 1];
            for (int i = 0; i < right.length; i++)
                right[i] = valuesToInsert[(nbOfValues - 1) / 2 + 1 + i];

            add(right);
            add(left);
        } else if (nbOfValues == 1)
            add(valuesToInsert[0]);
    }

    public void add(Point aPoint) {
        if (root == null)
            root = new Node(aPoint, null);
        else 
            root.add(aPoint);
    }
    
    public Object[] rangeQuery(double lowerBound, double upperBound) {
        Object[] result;
        Node splitNode = findSplitNode(lowerBound, upperBound);
        
        if (splitNode != null) {
            if (splitNode.isLeaf() && splitNode.point.value >= lowerBound && splitNode.point.value <= upperBound) 
                result = new Point[] {splitNode.point};
            else {
                result = new Point[0];
                ArrayList<Point> leaves = new ArrayList<Point>();
                // Left subtree, path to lowerBound
                Node currentNode = splitNode.left;
                while (!currentNode.isLeaf()) {
                    if (lowerBound <= currentNode.point.value) {
                        currentNode.right.reportSubtree(leaves);
                        currentNode = currentNode.left;
                    } else
                        currentNode = currentNode.right;
                }
                if (currentNode.point.value >= lowerBound)
                    leaves.add(currentNode.point);
                
                // Right subtree, path to upperBound
                currentNode =  splitNode.right;
                while (!currentNode.isLeaf()) {
                    if (upperBound > currentNode.point.value) {
                        currentNode.left.reportSubtree(leaves);
                        currentNode = currentNode.right;
                    } else
                        currentNode = currentNode.left;
                }
                if (currentNode.point.value <= upperBound)
                    leaves.add(currentNode.point);
                
                result = leaves.toArray();
            }
        } else
            result = null;
        return result;
    }
    
    public int numberInRange(double lowerBound, double upperBound) {
        int result;
        Node splitNode = findSplitNode(lowerBound, upperBound);
        
        if (splitNode != null) {
            if (splitNode.isLeaf() && splitNode.point.value >= lowerBound && splitNode.point.value <= upperBound) 
                result = 1;
            else {
                result = 0;
                // Left subtree, path to lowerBound
                Node currentNode =  splitNode.left;
                while (!currentNode.isLeaf()) {
                    if (lowerBound <= currentNode.point.value) {
                        int nbOfLeaves = currentNode.right.numberOfLeaves;
                        currentNode = currentNode.left;
                        result += nbOfLeaves;
                    } else
                        currentNode = currentNode.right;
                }
                if (currentNode.point.value >= lowerBound)
                    result ++;
                
                // Right subtree, path to upperBound
                currentNode =  splitNode.right;
                while (!currentNode.isLeaf()) {
                    if (upperBound > currentNode.point.value) {
                        int nbOfLeaves = currentNode.left.numberOfLeaves;
                        currentNode = currentNode.right;
                        result += nbOfLeaves;
                    } else
                        currentNode = currentNode.left;
                }
                if (currentNode.point.value <= upperBound)
                    result ++;
            }
        } else
            result = 0;
        return result;
    }
    
    public double maxWeightInRange(double lowerBound, double upperBound) {
        double result;
        Node splitNode = findSplitNode(lowerBound, upperBound);
        
        if (splitNode != null) {
            if (splitNode.isLeaf() && splitNode.point.value >= lowerBound && splitNode.point.value <= upperBound) 
                result = splitNode.point.weight;
            else {
                result = 0;
                // Left subtree, path to lowerBound
                Node currentNode = splitNode.left;
                while (!currentNode.isLeaf()) {
                    if (lowerBound <= currentNode.point.value) {
                        double subtreeMaxWeight = currentNode.right.point.weight;
                        currentNode = currentNode.left;
                        result = Math.max(result, subtreeMaxWeight);
                    } else
                        currentNode = currentNode.right;
                }
                if (currentNode.point.value >= lowerBound)
                    result = Math.max(result, currentNode.point.weight);
                
                // Right subtree, path to upperBound
                currentNode = splitNode.right;
                while (!currentNode.isLeaf()) {
                    if (upperBound > currentNode.point.value) {
                        double subtreeMaxWeight = currentNode.left.point.weight;
                        currentNode = currentNode.right;
                        result = Math.max(result, subtreeMaxWeight);
                    } else
                        currentNode = currentNode.left;
                }
                if (currentNode.point.value <= upperBound)
                    result = Math.max(result, currentNode.point.weight);
            }
        } else
            result = 0;
        return result;
    }
    
    private Node findSplitNode(double lowerBound, double upperBound) {
        if (root != null) {
            Node currentNode = root;
            while (!currentNode.isLeaf() && (currentNode.point.value >= upperBound || currentNode.point.value < lowerBound)) {
                if (currentNode.point.value >= upperBound)
                    currentNode = currentNode.left;
                else
                    currentNode = currentNode.right;
            }
            return currentNode;
        }
        return null;
    }
    
    private static Point[] arrayConcat(Point[] first, Point[] second) {
        Point[] result = new Point[first.length + second.length];
        for (int i = 0; i < first.length; i++)
            result[i] = first[i];
        for (int i = 0; i < second.length; i++)
            result[first.length + i] = second[i];
        return result;
    }

    public void printTree() {
        if (root != null) {
            root.printTree();
        }
    }
    
    protected void setRoot(Node newRoot) {
        root = newRoot;
    }
    
    public static void mergeSort(Point[] unsortedArray) {
        Point[] tmp = new Point[unsortedArray.length];
        mergeSort(unsortedArray, tmp, 0, unsortedArray.length - 1);
    }
            
    private static void mergeSort(Point[] unsortedArray, Point[] tmp, int start, int end) {
        if(start < end) {
            int center = (start + end)/2;
            mergeSort(unsortedArray, tmp, start, center);
            mergeSort(unsortedArray, tmp, center+1, end);
            merge(unsortedArray, tmp, start, center+1, end);
        }
    }
    
    private static void merge(Point[] unsortedArray, Point[] tmp, int left, int right, int rightEnd) {
        int tmpIndex = left;
        int leftEnd = right - 1;
        int objectSorted = rightEnd - left + 1;

        while(left <= leftEnd && right <= rightEnd) {
            if(unsortedArray[left].value < unsortedArray[right].value) {
                tmp[tmpIndex++] = unsortedArray[left++];
            } else {
                tmp[tmpIndex++] = unsortedArray[right++];
            }
        }
        
        while(left <= leftEnd)
            tmp[tmpIndex++] = unsortedArray[left++];
        while(right <= rightEnd)
            tmp[tmpIndex++] = unsortedArray[right++];
        
        for(int i = 0; i < objectSorted; i++)
            unsortedArray[rightEnd-i] = tmp[rightEnd-i]; // reverse copy because we still know rightEnd but not left
    }
}
