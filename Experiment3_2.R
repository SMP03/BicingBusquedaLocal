# base = "Overnight" #Posar el nom base especificat a l'script
# kVals = c(50, 75, 100, 125, 150)
# lambdaVals = c(0.00125, 0.00025, 0.00005, 0.00001)
# decpoint = "."

# base = "Experiment3_2Data/test" #Posar el nom base especificat a l'script
# kVals = c(1, 25, 50, 100, 200)
# lambdaVals = c(1, 0.01, 0.001, 0.0001, 0.00001)
# decpoint = "."

base = "Experiment3_2_2Data/test" #Posar el nom base especificat a l'script
kVals = c(150, 175, 200, 225, 250)
lambdaVals = c(0.0005, 0.0003, 0.0001, 0.00007, 0.00005)
decpoint = "."


HCData <- read.table(paste(base, "_HC.txt", sep=""), header=TRUE, sep="\t", dec=decpoint)
nreps = nrow(HCData)
nk = length(kVals)
nl = length(lambdaVals)

k <- rep(kVals, each=(nreps*nl))
lambda <- rep(lambdaVals, nk, each=nreps)
TotalProfit = c()
nodes = c()
diffHC = c()

for (i in c(0:(nk-1))) {
  for (j in c(0:(nl-1))) {
    tempData <-read.table(paste(base, "_k", i, "l", j, ".txt", sep=""), header=TRUE, sep="\t", dec=decpoint)
    for (r in c(0:(nreps-1))) {
      #print(paste(i, j, r, 1+i*nl*nreps + j*nreps + r, sep=" "))
      TotalProfit[1+i*nl*nreps + j*nreps + r] = tempData$TotalProfit[1+r]
      nodes[1+i*nl*nreps + j*nreps + r] = tempData$NodesExpanded[1+r]
      hcProfit <- HCData$TotalProfit[HCData$MapSeed==tempData$MapSeed[1+r] & HCData$InitStratSeed==tempData$InitStratSeed[1+r]]
      diffHC[1+i*nl*nreps + j*nreps + r] = tempData$TotalProfit[1+r] - hcProfit
    }
  }
}

expData <- data.frame(k, lambda,TotalProfit, nodes, diffHC)
means <- matrix(data=NA, nrow=nk, ncol=nl)
diffMeans <- matrix(data=NA, nrow=nk, ncol=nl)

for (row in 1:nk) {
  for (col in 1:nl) {
    means[row,col] = mean(expData$TotalProfit[expData$k==kVals[[row]] & expData$lambda==lambdaVals[[col]]])
    diffMeans[row,col] = mean(expData$diffHC[expData$k==kVals[[row]] & expData$lambda==lambdaVals[[col]]])
  }
}


means <- data.frame(means)
rownames(means) = kVals
colnames(means) = lambdaVals

diffMeans <- data.frame(diffMeans)
rownames(diffMeans) = kVals
colnames(diffMeans) = lambdaVals

