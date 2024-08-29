(ns aceitacao.financeiro.transacoes-test
  (:require [midje.sweet :refer :all]
            [financeiro.transacoes :refer :all]))


(fact "Uma transacao sem valor nao eh valida"
      (valida? {:tipo "receita"}) => false)


(fact "Uma transacao com valor negatiov nao eh valida"
      (valida? {:valor -100 :tipo "receita"}) => false)


(fact "Uma transacao com valor nao numerico nao eh valida"
      (valida? {:valor "mil" :tipo "receita"}) => false)


(fact "Uma transacao sem tipo nao eh valida"
      (valida? {:valor 90}) => false)


(fact "Uma transacao com tipo desconhecido nao eh valida"
      (valida? {:valor 8 :tipo "investimento"}) => false)


(fact "Uma transacao com valor numerico positivo e com tipo conhecido eh valida"
      (valida? {:valor 230 :tipo "receita"}) => true)
