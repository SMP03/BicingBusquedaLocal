# Script experimento 3 tabla de valores del beneficio obtenido para combinaciones de valores de k y v
base = "Experiment3_PCLUCA_50Reps/test" #Posar el nom base especificat a l'script
kVals = c(1, 25, 50, 100, 200) #Posar valors de k especificats a l'script generador (en el mateix ordre)
lambdaVals = c(0.1, 0.01, 0.001, 0.0001, 0.00001) #Posar valors de lambda especificats a l'script generador (en el mateix ordre)
decpoint = ","


HCData <- read.table(paste(base, "_HC.txt", sep=""), header=TRUE, sep="\t", dec=decpoint)
nreps = nrow(HCData)
nk = length(kVals)
nl = length(lambdaVals)

k <- c()
lambda <- c()
TotalProfit = c()
nodes = c()
diffHC = c()

for (i in c(0:(nk-1))) {
  for (j in c(0:(nl-1))) {
    tempData <-read.table(paste(base, "_k", i, "l", j, ".txt", sep=""), header=TRUE, sep="\t", dec=decpoint)
    for (r in c(0:(nreps-1))) {
      #print(paste(i, j, r, 1+i*nl*nreps + j*nreps + r, sep=" "))
      index = 1 + (i*nl*nreps) + (j*nreps) + r
      TotalProfit[index] = tempData$TotalProfit[1+r]
      nodes[index] = tempData$NodesExpanded[1+r]
      k[index] = kVals[i+1]
      lambda[index] = lambdaVals[j+1]
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

# Plot de barras tridimensional
# library("plot3D")
# k = rep(kVals[1:nk], each=nl)
# lambda = rep(lambdaVals[1:nl], nk)
# totalProfitMean = rep(0, nk*nl)
# meansExpanded = data.frame(k, lambda, totalProfitMean)
# for (row in 1:nrow(meansExpanded)) {
#   meansExpanded$totalProfitMean[row] = mean(expData$TotalProfit[expData$k==meansExpanded$k[row] & expData$lambda==meansExpanded$lambda[row]])
# }
# hist3D(z=means, x=kVals, y=lambdaVals)
# 
# hist3D(z=means, scale = FALSE, expand = 0.01, bty = "g", phi = 20,
#        col = "#0072B2", border = "black", shade = 0.2, ltheta = 90,
#        space = 0.3, ticktype = "detailed", d = 2)

means <- data.frame(means)
rownames(means) = kVals
colnames(means) = lambdaVals


diffMeans <- data.frame(diffMeans)
rownames(diffMeans) = kVals
colnames(diffMeans) = lambdaVals

View(means)
