base = "Experiment7Data/exp7_SA_Heu2" 

Equilibrium <- read.table(paste(base,"EQ_.txt",sep="_"), header=TRUE,sep='\t', dec=",")
RushHour <- read.table(paste(base,"RH_.txt",sep="_"), header=TRUE,sep='\t', dec=",")

boxplot(Equilibrium$TotalProfit~Equilibrium$NumFurgos, outpch=4, col=brewer.pal(n=3, name="Blues"),
        main="Beneficio total en función del número de furgonetas Demanda Equilibrada", xlab="Número Furgonetas",ylab="Beneficio Total (€)")

boxplot(RushHour$TotalProfit~RushHour$NumFurgos, outpch=4, col=brewer.pal(n=3, name="Blues"),
        main="Beneficio total en función del número de furgonetas Rush Hour", xlab="Número Furgonetas",ylab="Beneficio Total (€)")
