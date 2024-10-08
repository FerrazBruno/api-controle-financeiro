(ns unitarios.financeiro.db-test
  (:require [midje.sweet :refer :all]
            [financeiro.db :refer :all]))


(facts "Guarda uma transação num átomo"
       (against-background [(before :facts (limpar))]

                           (fact "a coleção de transações inicia vazia"
                                 (count (transacoes)) => 0)

                           (fact "a transação é o primeiro registro"
                                 (registrar {:valor 7 :tipo "receita"}) => {:id 1 :valor 7 :tipo "receita"}
                                 (count (transacoes)) => 1)))


(facts "Calcula o saldo dada uma coleção de transações"
       (against-background [(before :facts (limpar))]

                           (fact "saldo é positivo quando só tem receita"
                                 (registrar {:valor 1 :tipo "receita"})
                                 (registrar {:valor 10 :tipo "receita"})
                                 (registrar {:valor 100 :tipo "receita"})
                                 (registrar {:valor 1000 :tipo "receita"})
                                 (saldo) => 1111)

                           (fact "saldo é negativo quando só tem despesa"
                                 (registrar {:valor 2 :tipo "despesa"})
                                 (registrar {:valor 20 :tipo "despesa"})
                                 (registrar {:valor 200 :tipo "despesa"})
                                 (registrar {:valor 2000 :tipo "despesa"})
                                 (saldo) => -2222)

                           (fact "saldo é a soma das receitas menos a soma das despesas"
                                 (registrar {:valor 2 :tipo "despesa"})
                                 (registrar {:valor 10 :tipo "receita"})
                                 (registrar {:valor 200 :tipo "despesa"})
                                 (registrar {:valor 1000 :tipo "receita"})
                                 (saldo) => 808)))


(facts "Filtra transações por tipo"
       (def transacoes-aleatorias
         '({:valor 2 :tipo "despesa"}
           {:valor 10 :tipo "receita"}
           {:valor 200 :tipo "despesa"}
           {:valor 1000 :tipo "receita"}))


       (against-background [(before :facts [(limpar)
                                            (doseq [transacao transacoes-aleatorias]
                                              (registrar transacao))])]


                           (fact "encontra apenas as receitas"
                                 (transacoes-do-tipo "receita") => '({:valor 10 :tipo "receita"}
                                                                     {:valor 1000 :tipo "receita"}))


                           (fact "encontra apenas as despesas"
                                 (transacoes-do-tipo "despesa") => '({:valor 2 :tipo "despesa"}
                                                                     {:valor 200 :tipo "despesa"}))))


(facts "Filtra transações por rótulo"
       (def transacoes-aleatorias
         '({:valor 7.0M :tipo "despesa" :rotulos ["sorvete" "entretenimento"]}
           {:valor 88.0M :tipo "despesa" :rotulos ["livro" "educacao"]}
           {:valor 106.0M :tipo "despesa" :rotulos ["curso" "educacao"]}
           {:valor 8000.0M :tipo "receita" :rotulos ["salario"]}))


       (against-background [(before :facts [(limpar)
                                            (doseq [transacao transacoes-aleatorias]
                                              (registrar transacao))])]


                           (fact "Encontra a transação com rótulo 'salario'"
                                 (transacoes-com-filtro {:rotulos "salario"}) => '({:valor 8000.0M :tipo "receita" :rotulos ["salario"]}))


                           (fact "Encontra as 2 transações com rótulo 'educacao'"
                                 (transacoes-com-filtro {:rotulos ["educacao"]}) => '({:valor 88.0M :tipo "despesa" :rotulos ["livro" "educacao"]}
                                                                                      {:valor 106.0M :tipo "despesa" :rotulos ["curso" "educacao"]}))


                           (fact "Encontra as 2 transações com rótulo 'livro' ou 'curso'"
                                 (transacoes-com-filtro {:rotulos ["livro" "curso"]}) => '({:valor 88.0M :tipo "despesa" :rotulos ["livro" "educacao"]}
                                                                                           {:valor 106.0M :tipo "despesa" :rotulos ["curso" "educacao"]}))))
