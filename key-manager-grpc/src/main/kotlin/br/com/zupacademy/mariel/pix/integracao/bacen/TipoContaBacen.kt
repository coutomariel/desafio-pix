package br.com.zupacademy.mariel.pix.integracao.bacen

import br.com.zupacademy.mariel.TipoConta

enum class TipoContaBacen {
    CACC {
        override fun convert(): TipoConta {
            return TipoConta.CONTA_CORRENTE
        }
    },
    CAGR {
        override fun convert(): TipoConta {
            return TipoConta.CONTA_POUPANCA
        }
    };

    abstract fun convert(): TipoConta
}