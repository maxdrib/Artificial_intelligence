/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;


/**
 * Do NOT edit implemented methods.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Class Decision Tree
 */
public class DecisionTree {
	/**
	 * Class Attribute
	 */
	public static class Attribute {
		/**
		 * Name of the attribute
		 */
		private final String name;

		/**
		 * Index of the column in the instances that corresponds to value of
		 * this attribute. Leftmost column has an index of zero.
		 */
		private final int columnIndex;

		/**
		 * All possible values that the attribute may take.
		 */
		private final String[] possibleAttrValues;

		/**
		 * Constructor.
		 *
		 * @param attributeInfo
		 *            String representation of all the fields of this class.
		 */
		public Attribute(final String attributeInfo) {
			final String[] fields = attributeInfo.split("\\s+");
			columnIndex = Integer.parseInt(fields[0]);
			name = fields[1];
			possibleAttrValues = Arrays.copyOfRange(fields, 2, fields.length);
		}
	}

	/**
	 * Class Instance
	 */
	private static class Instance {
		/**
		 * Label of an instance
		 */
		private final Label label;

		/**
		 * Features of an instance. These are values of all the attributes for
		 * this instance.
		 */
		private final String[] features;

		/**
		 * Constructor.
		 *
		 * @param featuresAndLabel
		 *            String representation of the fields of this class.
		 */
		public Instance(final String featuresAndLabel) {
			final String[] fields = featuresAndLabel.split("\\s+");
			features = Arrays.copyOfRange(fields, 0, fields.length - 1);
			label = Label.valueOf(fields[fields.length - 1]);
		}

		/**
		 * Get value of an attribute in this instance. May use while creating
		 * splits on an attribute-value pair.
		 *
		 * @param attr
		 *            Attribute
		 * @return Value
		 */
		public final String getValueForAttribute(final Attribute attr) {
			return features[attr.columnIndex];
		}
	}

	/**
	 * Possible Labels of an instance
	 */
	private static enum Label {
		YES, NO
	}

	/**
	 * @param args
	 *            Paths to input files.
	 */
	public static void main(final String[] args) {
		final List<Attribute> attributeInfo = readAttributes(args[0]);
		final List<Instance> trainingData = readInstances(args[1]);
		final DecisionTree tree = new DecisionTree(attributeInfo, trainingData);
		if(args.length > 3) {
			if(args[3].equalsIgnoreCase("prune")){
				// Only for bonus credit.
				System.out.println("Pruning not implemented"); // Comment this out if you do implement!
				/*
				 * Add code to call your pruning method(s) here as appropriate.
				 * For example: tree.prune(arguments...);
				 */
			}
		}
		tree.print();
		System.out.println("\n");
		final List<Instance> testData = readInstances(args[2]);
		for (final Instance testInstance : testData) {
			System.out.println(tree.classify(testInstance));
		}
		System.out.println("\n");
		System.out.println("Training error = " + tree.computeError(trainingData) + " , Test error = "
				+ tree.computeError(testData));
	}

	/**
	 * To parse the attribute info file.
	 *
	 * @param attrInfoPath
	 *            file path
	 * @return List of attributes (objects of Class Attribute)
	 */
	public static List<Attribute> readAttributes(final String attrInfoPath) {
		final List<Attribute> attributes = new ArrayList<>();
		BufferedReader br = null;

		try {
			String currentLine;

			br = new BufferedReader(new FileReader(attrInfoPath));

			while ((currentLine = br.readLine()) != null) {
				final Attribute attribute = new Attribute(currentLine);
				attributes.add(attribute);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
		return attributes;
	}

	/**
	 * To parse the training data (instances)
	 *
	 * @param trainDataPath
	 *            file path
	 * @return List of Instances.
	 */
	public static List<Instance> readInstances(final String trainDataPath) {
		final List<Instance> instances = new ArrayList<>();
		BufferedReader br = null;

		try {
			String currentLine;

			br = new BufferedReader(new FileReader(trainDataPath));
			br.readLine();

			while ((currentLine = br.readLine()) != null) {
				final Instance instance = new Instance(currentLine);
				instances.add(instance);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
		return instances;
	}

	/**
	 * The attribute which is the root of this tree.
	 */
	private final Attribute rootAttribute;

	/**
	 * True if this tree is a leaf.
	 */
	private final Boolean isLeaf;

	/**
	 * The label to be output if this tree is a leaf; Set to Null if the
	 * 'isleaf' flag is false.
	 */
	private final Label leafVal;

	/**
	 * List of the children trees sorted in the same order as the corresponding
	 * values of the root attribute.
	 */
	private final List<DecisionTree> children;

	/**
	 * Constructor. Builds the tree given the following parameters.
	 *
	 * @param attributeList
	 *            List of attributes
	 * @param instanceList
	 *            List of instances
	 */
	public DecisionTree(final List<Attribute> attributeList, final List<Instance> instanceList) {
		isLeaf = shouldThisBeLeaf(instanceList, attributeList);
		if (isLeaf) {
			leafVal = computeLeafLabel(instanceList);
			rootAttribute = null;
			children = null;
			return;
		}
		leafVal = null;
		rootAttribute = computeBestAttribute(attributeList, instanceList);
		final List<Attribute> remAttributeList = getRemainingAttributes(attributeList, rootAttribute);
		children = new ArrayList<>();
		for (final String possibleVal : rootAttribute.possibleAttrValues) {
			children.add(new DecisionTree(remAttributeList,
				generateSplitForAttrVal(instanceList, rootAttribute, possibleVal)));
		}
	}
	/**
	 * Classify an instance. May also be used when evaluating performance on test data.
	 *  Helper function for classify, uses recursion
	 * @param instance
	 *            Instance to be classified.
	 * @param tree
	 *            Subtree which is currently being explored.
	 * @return Label output
	 */
	public Label classify(final Instance instance, final DecisionTree tree) {
                // Explore subtree if not leaf. Determine correct child tree to follow
		if (!tree.isLeaf) {
                    int index = 0;
                    String value = instance.getValueForAttribute(tree.rootAttribute);
                    for (String a : tree.rootAttribute.possibleAttrValues)
                    {
                        if(a.equals(value))
                            break;
                        index++;
                    }
                    // then recursive call
                    return classify(instance, tree.children.get(index));
                // If leaf, return value
		} else {
                    return tree.leafVal;
		}
	}
        
        
	/**
	 * Classify an instance. May also be used when evaluating performance on test data.
	 *
	 * @param instance
	 *            Instance to be classified.
	 * @return Label output
	 */
	public Label classify(final Instance instance) {            
            return classify(instance, this);
	}

	/**
	 * Computes the best attribute (least entropy)
	 *
	 * @param attributeList
	 *            List of attributes
	 * @param instanceList
	 *            List of instances
	 * @return The best attribute
	 */
	private Attribute computeBestAttribute(final List<Attribute> attributeList, final List<Instance> instanceList) {
            
            // Initialize variables
            Attribute bestAttribute = null;
            if (!attributeList.isEmpty())
                bestAttribute = attributeList.get(0);
            else
            {
                System.out.println("Empty list");
                return bestAttribute;
            }
            float bestEntropy = 1;
            
            // Iterate through potential attributes to find the best one
            for (Attribute a: attributeList)
            {
                // 3 lists. One for numPositive, one for numNegative, one for probabilities of internal nodes
                int[] numPositives = new int[(a.possibleAttrValues).length];
                int[] numNegatives = new int[(a.possibleAttrValues).length];
                float[] probabilities = new float[(a.possibleAttrValues).length];
                
                // Populate arrays such that arr[index] gives info on that internal node. How many +, how many -, and likelihood
                for (Instance i : instanceList)
                {
                    // Find which index should be updated (basically determined by value of instance)
                    int index = Arrays.asList(a.possibleAttrValues).indexOf(i.getValueForAttribute(a));
                    if (i.label.equals(Label.YES))
                        numPositives[index]++;
                    else
                        numNegatives[index]++;
                }
                
                // Populate probabilities array using same index as previously, 1 entry per attribute value
                int count = instanceList.size();
                for (int j = 0; j < (a.possibleAttrValues).length; j++)
                {
                    probabilities[j] = ((float)numPositives[j] + (float)numNegatives[j]) / (float) count;
                }
                
                // Calculate entropy by iterating through all resulting internal nodes
                // and calculating their entropies, then scaledly averaging them
                float entropy = 0;
                for (int j = 0; j < (a.possibleAttrValues).length; j++)
                {
                    // Find entropy of single inode
                    float ratioP = ((float)(numPositives[j]))/(numPositives[j]+numNegatives[j]);
                    float nodeEntropyP = (float) (-1*ratioP*Math.log(ratioP) / Math.log(2));
                    float ratioN = 1 - ratioP;
                    float nodeEntropyN = (float) (-1*ratioN*Math.log(ratioN) / Math.log(2));
                    float nodeEntropy = nodeEntropyN + nodeEntropyP;
                    if(numPositives[j] == 0 || numNegatives[j] == 0) continue;
                    entropy += nodeEntropy * probabilities[j];
                }
                
                // If new entropy is better than previous, save new best attribute and best entropy
                if (entropy < bestEntropy)
                {
                    bestEntropy = entropy;
                    bestAttribute = a;
                }
            }
            return bestAttribute;
	}

	/**
	 * Evaluate performance of this tree.
	 * 
	 * @param trainingData
	 * @return
	 */
	private double computeError(final List<Instance> trainingData) {
		int incorrect = 0;
                int total = trainingData.size();
                // Iterate through training data, classify each one
                // incrementing "incorrect" if result is not right
                for(Instance i : trainingData)
                    if(!classify(i).equals(i.label))
                        incorrect++;
		return (double)(incorrect)/total;
	}

	/**
	 * computes the label to be output at a leaf (which minimizes error on
	 * training data). If the given split is empty, you can assign any label for
	 * this leaf.
	 *
	 * @param instanceList
	 *            List of instances
	 * @return computed label
	 */
	private Label computeLeafLabel(final List<Instance> instanceList) {
		int numPositive = 0;
                int numNegative = 0;
                
                // Find number of positives and number of negatives at the node
                for (Instance i : instanceList)
                {
                    if (i.label.equals(Label.YES))
                        numPositive++;
                    else
                        numNegative++;
                }
                
                // Assign label of whichever is dominant, + or -
                if (numPositive >= numNegative)
                    return Label.YES;
                else
                    return Label.NO;
	}

	/**
	 * Split the data on an attribute-value pair.
	 * 
	 * @param instanceList
	 *            List of instances
	 * @param splitAttribute
	 *            Attribute to split on
	 * @param splitVal
	 *            Value to split on
	 * @return List of instances that constitute the said split (i.e. have the
	 *         given value for the given attribute)
	 */
	private List<Instance> generateSplitForAttrVal(final List<Instance> instanceList, final Attribute splitAttribute,
			final String splitVal) {
            
                // Iterate through instance list and add any matches to "splitList"
		List<Instance> splitList = new ArrayList<Instance>();
                for (Instance i : instanceList)
                {
                    if (i.getValueForAttribute(splitAttribute).equals(splitVal))
                        splitList.add(i);
                }
		return splitList;
	}

	/**
	 * @param attributeList
	 *            List of candidate attributes at this subtree
	 * @param rootAttribute
	 *            Attribute chosen as the root
	 * @return List of remaining attributes
	 */
	private List<Attribute> getRemainingAttributes(final List<Attribute> attributeList, final Attribute rootAttribute) {
            List<Attribute> remainingAttributes = new ArrayList<Attribute>();
            
            // Iterate through attributes and add whichever ones aren't "rootAttribute"
            for (Iterator<Attribute> iter = attributeList.listIterator(); iter.hasNext(); ) {
                    Attribute i = iter.next();
                    if (!i.equals(rootAttribute)) {
                        remainingAttributes.add(i);
                    }
            }
            return remainingAttributes;
	}

	/**
	 * Print a representation of this tree.
	 */
	public void print() {
		print(0);
	}

	/**
	 * Print relative to a calling super-tree.
	 *
	 * @param rootDepth
	 *            Depth of the root of this tree in the super-tree.
	 */
	private void print(final int rootDepth) {
		if (!isLeaf) {
			final Iterator<DecisionTree> itr = children.iterator();
			for (final String possibleAttrVal : rootAttribute.possibleAttrValues) {
				printIndent(rootDepth);
				System.out.println(rootAttribute.name + " = " + possibleAttrVal + " :");
				itr.next().print(rootDepth + 1);
			}
		} else {
			printIndent(rootDepth);
			System.out.println(leafVal);
		}
	}

	/**
	 * For formatted printing.
	 *
	 * @param n
	 *            Indent
	 */
	private void printIndent(final int n) {
		for (int i = 0; i < n; i++)
			System.out.print("\t");
	}

	/**
	 * Determine if this is simply a leaf, as a function of the given
	 * parameters.
	 *
	 * @param instanceList
	 *            List of instances
	 * @param attributeList
	 *            List of attributes
	 * @return True iff this tree should be a leaf.
	 */
	private boolean shouldThisBeLeaf(final List<Instance> instanceList, final List<Attribute> attributeList) {
            boolean allYes = true;
            boolean allNo = true;
            
            // If all instances have same label, then it should be a leaf
            for (final Instance instance : instanceList) {
				if (instance.label.equals(Label.YES))
                                    allNo = false;
                                if (instance.label.equals(Label.NO))
                                    allYes = false;
		}
            return allYes || allNo;
	}
}