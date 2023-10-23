base = "exp2"

set1 <- read.table(paste(base, "set1.txt", sep="_"), header=TRUE, sep="\t")
set2 <- read.table(paste(base, "set2.txt", sep="_"), header=TRUE, sep="\t")
set3 <- read.table(paste(base, "set3.txt", sep="_"), header=TRUE, sep="\t")
set1["OpSet"] <- rep(1, nrow(set1))
set2["OpSet"] <- rep(2, nrow(set2))
set3["OpSet"] <- rep(3, nrow(set3))


sets = rbind(set1,set2,set3)
b <- boxplot(sets$ExecutionTime ~ sets$OpSet)
b <- boxplot(sets$TotalProfit ~ sets$OpSet)

