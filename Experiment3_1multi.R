base = "testHeur0" #Posar el nom base especificat a l'script
kVals = c(1, 25, 125)
lambdaVals = c(0.1, 0.01, 0.001, 0.0001)
decpoint = "."
noms<-c("Iteration", "SAProfits")
nk = length(kVals)
nl = length(lambdaVals)


HCprofits <- read.table(paste(base, "_HC_trace.txt", sep=""), header=FALSE, sep="\t", dec=decpoint)
HCMaxProfit <- HCprofits[which.max(HCprofits$V1),]

tempData = c()
for (i in c(0:(nk-1))) {
  for (j in c(0:(nl-1))) {
    tempData[1+i*nl+j] <-read.table(paste(base, "_k", i, "l", j, "_trace.txt", sep=""), header=FALSE, sep="\t", dec=decpoint)
  }
}

ymin = min(tempData[[1]])
ymax = max(tempData[[1]])
xmax = length(tempData[[1]])
for (i in c(2:length(tempData))) {
  aux = min(tempData[[i]])
  if (aux < ymin) {ymin = aux}
  aux = max(tempData[[i]])
  if (aux > ymax) {ymax = aux}
  if (length(tempData[[i]]) > xmax) {
    xmax = length(tempData[[i]])
  }
}

layout(matrix(c(1:(nk*nl)), nk, nl, byrow=TRUE))
for (i in c(0:(nk-1))) {
  for (j in c(0:(nl-1))) {
    expData <- data.frame(c(0:(length(tempData[[1+i*nl+j]])-1)), tempData[[1+i*nl+j]])
    colnames(expData) = noms
    par(mar=c(3, 3, 1, 1))
    plot(expData$Iteration, expData$SAProfits, type="l", ylim=c(ymin, ymax), xlim=c(0,xmax), xlab="", ylab="")
    abline(h=HCMaxProfit, col="red")
    title(paste(base, " k:", kVals[i+1], " lambda:", lambdaVals[j+1], sep=""))
  }
}

