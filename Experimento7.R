base = "Experiment7Data/exp7_SA_Heu2" 

Equilibrium <- read.table(paste(base,"EQ_.txt",sep="_"), header=TRUE,sep='\t', dec=",")
RushHour <- read.table(paste(base,"RH_.txt",sep="_"), header=TRUE,sep='\t', dec=",")

EquilibriumF10 <- Equilibrium[Equilibrium$NumFurgos == 10,]
EquilibriumF15 <- Equilibrium[Equilibrium$NumFurgos == 15,]

RushHourF10 <- RushHour[RushHour$NumFurgos == 10,]
RushHourF15 <- RushHour[RushHour$NumFurgos == 15,]


boxplot(Equilibrium$TotalProfit~Equilibrium$NumFurgos, outpch=4, col=brewer.pal(n=3, name="Blues"),
        main="Beneficio total en función del número de furgonetas Demanda Equilibrada", xlab="Número Furgonetas",ylab="Beneficio Total (€)")

boxplot(RushHour$TotalProfit~RushHour$NumFurgos, outpch=4, col=brewer.pal(n=3, name="Blues"),
        main="Beneficio total en función del número de furgonetas Rush Hour", xlab="Número Furgonetas",ylab="Beneficio Total (€)")

t.test(EquilibriumF10$TotalProfit,EquilibriumF15$TotalProfit,paired=T)
t.test(RushHourF10$TotalProfit,RushHourF15$TotalProfit,paired=T)