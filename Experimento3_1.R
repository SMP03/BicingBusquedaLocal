# Script experimento 3 gráfico individual beneficio~iteración
base = "fig6_3_3" #Posar el nom base especificat a l'script
noms<-c("Iteration", "SAProfits")

SAprofits <- read.table(paste(base, "_SA_trace.txt", sep=""), header=FALSE, sep="\t")
HCprofits <- read.table(paste(base, "_HC_trace.txt", sep=""), header=FALSE, sep="\t")

HCMaxProfit <- HCprofits[which.max(HCprofits$V1),]

expData <- data.frame(c(0:(nrow(SAprofits)-1)), SAprofits)
colnames(expData) = noms

ymin = min(HCprofits[which.min(HCprofits$V1),], SAprofits[which.min(SAprofits$V1),])
ymax = max(HCprofits[which.max(HCprofits$V1),], SAprofits[which.max(SAprofits$V1),])
  
plot(expData$Iteration, expData$SAProfits, type="l", ylim=c(ymin, ymax), main="Beneficio en función de la iteración con k=25, λ=0.0001",
     ylab="Beneficio obtenido (€)", xlab="Número de iteración")
abline(h=HCMaxProfit, col="red")

