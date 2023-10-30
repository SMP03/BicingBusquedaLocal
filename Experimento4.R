# Script experimento 4
base = "test" #Posar el nom base especificat a l'script
min = 25
max = 200
step = 25

expData <- read.table(paste(base, "_stations.txt", sep=""), header=FALSE, sep="\t")
HCprofits <- read.table(paste(base, "_HC_trace.txt", sep=""), header=FALSE, sep="\t")

HCMaxProfit <- HCprofits[which.max(HCprofits$V1),]

expData <- data.frame(c(0:(nrow(SAprofits)-1)), SAprofits)
colnames(expData) = noms

ymin = min(HCprofits[which.min(HCprofits$V1),], SAprofits[which.min(SAprofits$V1),])
ymax = max(HCprofits[which.max(HCprofits$V1),], SAprofits[which.max(SAprofits$V1),])
