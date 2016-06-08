import sys
import numpy as np
from math import exp
import time

#################################################################
# Name: classifier.py											#
# Author: Max Dribinsky											#
#																#
# Program to classify 100 attributes, 							#
# taking in and assigning binary labels 						#
################################################################# 
# Takes 2 arguments from command line 							#
# -Argument 1 = file of training data 							#
# -Argument 2 = file of testing data 							#
# -Argument 3 = integer value for number of hidden neurons		#
# -Argument 4 = float value for learning rate (alpha)			#
#################################################################

########################## CONSTANTS ############################
NUM_ATTRIBUTES = 100
NUM_HIDDEN_NEURONS = int(sys.argv[3])
ALPHA = float(sys.argv[4])


########################## GLOBAL VARS ###########################
# Weight array to compute activation of hidden neurons (divide by 1000 to have small numbers)
weights1 = np.random.rand(NUM_HIDDEN_NEURONS,NUM_ATTRIBUTES)/1000
# Bias vector to compute activation of hidden neurons (divide by 1000 to have small numbers)
bias1 = np.random.rand(NUM_HIDDEN_NEURONS)/1000
# Weight vector to compute activation of output (divide by 1000 to have small numbers)
weights2 = np.random.rand(NUM_HIDDEN_NEURONS)/1000
# Bias scalar to compute activation of output neuron (divide by 1000 to have small numbers)
bias2 = np.random.rand()/1000

# Set sizes get populated in main when reading input files
LEARNING_SET_SIZE = 0
EVALUATION_SET_SIZE = 0


########################### FUNCTIONS #############################
############## PERFORM ON ONE INSTANCE AT A TIME ##################
def calc_x1(y0):
	x1 = np.dot(weights1, y0) + bias1
	return x1

# Equivalent to g(x)
def calc_y(x):
	y = 1.0/(1+np.exp(-1*x))
	return y

def calc_x2(y1):
	x2 = np.dot(weights2, y1) + bias2
	return x2

############### PERFORMED ON ALL INSTANCES AT ONCE ###############
def g_prime(x):
	g_p = np.multiply(calc_y(x), (1-calc_y(x)))
	return g_p


def calc_square_error(t, y2):
	# Error equals 1/2 summation from 1 to n of (t[i] - y2[i])
	differences = np.subtract(t,  y2)
	differences_squared = np.multiply(differences, differences)
	error = np.sum(differences_squared/2)
	return error


def calc_delta2(x2, y2, t):
	difference = np.subtract(y2,t)	
	result = np.multiply(g_prime(x2), np.subtract(y2,t))
	return result

def calc_delta1(x1, x2, y2, t):
	g_p = g_prime(x1)
	weights2T = np.transpose(weights2)
	delta2 = calc_delta2(x2,y2,t)
	w2Tdelta2 = np.empty(g_p.shape)
	for instance in xrange(len(delta2)):
		w2Tdelta2[instance] = delta2[instance]*weights2T
	return np.multiply(g_p, w2Tdelta2)



def calc_gradient_error_w2(x2,y1,y2,t):
	error = np.random.rand(NUM_HIDDEN_NEURONS)
	delta2 = calc_delta2(x2,y2,t)
	y1T = np.transpose(y1)
	product = np.multiply(delta2, y1T)
	summation = np.sum(product, axis=1)
	return summation

def calc_gradient_error_w1(y0, x1,x2,y2,t):
	delta1 = calc_delta1(x1,x2,y2,t)
	y0T = np.transpose(y0)
	product = np.dot(y0T, delta1)
	error = np.sum(product, axis=1)
	return error



def calc_gradient_error_b2(x2,y2,t):
	delta2 = calc_delta2(x2,y2,t)
	error = np.sum(delta2)
	# Should return a single float value
	return error

def calc_gradient_error_b1(x1,x2,y2,t):
	delta1 = calc_delta1(x1,x2,y2,t)
	error = np.sum(delta1, axis=0)
	return error



def update_weight_arrays(y0,x1,x2,y1,y2,t):
	global weights2
	global weights1
	global bias2
	global bias1


	# Calculate errors for each weight and bias array/matrix/value
	gradient_error_w1 = calc_gradient_error_w1(y0,x1,x2,y2,t)
	gradient_error_w2 = calc_gradient_error_w2(x2,y1,y2,t)
	gradient_error_b1 = calc_gradient_error_b1(x1,x2,y2,t)
	gradient_error_b2 = calc_gradient_error_b2(x2,y2,t)

	# Calculate new values, store in temporary variables
	new_w1 = weights1 - ALPHA*gradient_error_w1
	new_w2 = weights2 - ALPHA*gradient_error_w2
	new_b1 = bias1 - ALPHA*gradient_error_b1
	new_b2 = bias2 - ALPHA*gradient_error_b2


	# Check if parameters have converged
	CONVERGES = 1

	# If any of parameters are not converged, then CONVERGES set to 0
	# where tolerance = .0001
	if(not np.allclose(new_w1, weights1, atol=1e-3)):
		CONVERGES = 0
	if(not np.allclose(new_w2, weights2, atol=1e-3)):
		CONVERGES = 0
	if(not np.allclose(new_b1, bias1, atol=1e-3)):
		CONVERGES = 0
	if(not np.allclose(new_b2, bias2, atol=1e-3)):
		CONVERGES = 0

	# Updates weights and biases with new values
	weights1 = new_w1
	weights2 = new_w2
	bias1 = new_b1
	bias2 = new_b2

	return CONVERGES

def learn(np_learning, np_targets):
	# Initialize numpy arrays
	np_y0 = np.array(np_learning)
	np_x1 = np.empty((LEARNING_SET_SIZE, NUM_HIDDEN_NEURONS))
	np_y1 = np.empty((LEARNING_SET_SIZE, NUM_HIDDEN_NEURONS))
	np_x2 = np.empty((LEARNING_SET_SIZE))
	np_y2 = np.empty((LEARNING_SET_SIZE))

	# Initializize logistical vars
	trial_num = 1
	error = 0


	# Learning loop continues until error converges
	while(True):
		# Calculate prediction for each instance, and save results to arrays for future use
		for instance in xrange(len(np_learning)):
			x1 = calc_x1(np_learning[instance])
			np_x1[instance] = x1
			try:
				y1 = calc_y(x1)
			except OverflowError:
				print "OVERFLOW ERROR y1:",x1
				
			np_y1[instance] = y1
			x2 = calc_x2(y1)
			np_x2[instance] = x2
			try:
				y2 = calc_y(x2)
			except OverflowError:
				print "OVERFLOW ERROR y2:",x2
				raise
			np_y2[instance] = y2


		# If error still exceeds condition, update weight arrays/vectors
		converges = update_weight_arrays(np_y0, np_x1, np_x2, np_y1, np_y2, np_targets)
		# Escape condition to check
		if (converges):
			break

		# Calculate error every time and print
		# print str(calc_square_error(np_targets, np_y2))

		# Update local trial values
		trial_num+=1

	# Calculate error of last prediction
	print "Number of iterations:", str(trial_num)
	print "Final error:", str(calc_square_error(np_targets, np_y2))



def evaluate(np_evaluation, np_targets):
	correct = 0
	random_correct = 0
	global EVALUATION_SET_SIZE
	# Calculate prediction for each given instance, and compare to actual label
	y2_arr = np.empty(EVALUATION_SET_SIZE)
	for instance in xrange(len(np_evaluation)):
		x1 = calc_x1(np_evaluation[instance])
		y1 = calc_y(x1)
		x2 = calc_x2(y1)
		y2 = calc_y(x2)
		if(int(round(y2))==np_targets[instance]):
			correct += 1
		if(int(round(np.random.rand()))==np_targets[instance]):
			random_correct += 1

		y2_arr[instance] = int(round(y2))

	# Calculate error and print results
	error = (EVALUATION_SET_SIZE - correct)/float(EVALUATION_SET_SIZE)
	print "ANN evaluation percent correct:\t\t%"+ str(100*(1-error))
	print "ANN evaluation percent incorrect:\t%"+ str(100*error)



def main():
	# Training file
	f = open(sys.argv[1],'r')
	# Evaluation file
	e = open(sys.argv[2],'r')

	# Declare variables
	learning_input_attr = []
	evaluation_input_attr = []
	learning_input_labels = []
	evaluation_input_labels = []


	# Count set sizes
	global LEARNING_SET_SIZE
	global EVALUATION_SET_SIZE
	LEARNING_SET_SIZE = sum(1 for line in f)
	EVALUATION_SET_SIZE = sum(1 for line in e)
	f.close()
	e.close()

	# Reopen files
	# Training file
	f = open(sys.argv[1],'r')
	# Evaluation file
	e = open(sys.argv[2],'r')

	print "Beginning classification with", str(NUM_HIDDEN_NEURONS), "hidden neurons and learning rate", str(ALPHA), "\n"

	#iterate through training file, instance by instance to construct input matrices
	for line in f:
		instance = line.split(',')
		# remove newline character from array
		instance[-1] = instance[-1][0]

		# Convert strings to integers
		instance = map(int, instance)

		# save first value as label, and add other attributes to attributes array
		learning_input_labels.append(instance[0])
		learning_input_attr.append(instance[1:])

	# Convert generated arrays to numpy arrays
	np_learning = np.array(learning_input_attr)
	np_learning_targs = np.array(learning_input_labels)

	# Call learning function on learning input, and time the learning"
	start_time = time.time()
	learn(np_learning, np_learning_targs)
	execution_time = time.time() - start_time
	print "Total learning time:", execution_time

	# Begin evaluation. Iterate through file
	for line in e:
		instance = line.split(',')

		# remove newline character from array
		instance[-1] = instance[-1][0]

		# Convert strings to integers
		instance = map(int, instance)

		# save first value as label
		evaluation_input_labels.append(instance[0])
		evaluation_input_attr.append(instance[1:])

	# Convert generated arrays to numpy arrays
	np_evaluation = np.array(evaluation_input_attr)
	np_eval_targs = np.array(evaluation_input_labels)

	# Call evaluation function on evaluation input
	evaluate(np_evaluation, np_eval_targs)

	# Close files
	f.close()
	e.close()


if __name__ == "__main__":
	main()
