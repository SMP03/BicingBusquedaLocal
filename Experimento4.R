# Script experimento 4
base = "Experiment4Data/test" #Posar el nom base especificat a l'script
# Omplir amb els paràmetres de l'execució
min = 25
max = 250
step = 25
reps = 10

expData <- read.table(paste(base, "_station.txt", sep=""), header=TRUE, sep="\t")

expData["NumStations"] = rep(seq(from=min, to=max, by=step), each = 10)

means = c()

for (i in seq(from=min, to=max, by=step)) {
  means <- append(means, mean(expData$ExecutionTime[expData$NumStations==i]))
}

plot(y=means, x=seq(from=min, to=max, by=step),
     xlab="Número de estaciones", ylab="Tiempo de ejecución (ms)")
