package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Entidade que representa um brinquedo esportivo (crianças de até 15 anos).
 * Mapeada para a tabela TDS_TB_BRINQUEDOS, com as colunas obrigatórias da AC4:
 * Id, Nome, Tipo, Classificacao, Tamanho e Preco.
 */
@Entity
@Table(name = "TDS_TB_BRINQUEDOS")
public class Brinquedo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O tipo é obrigatório (ex.: Bola, Patins, Bicicleta).")
    @Size(max = 50, message = "O tipo deve ter no máximo 50 caracteres.")
    @Column(name = "TIPO", nullable = false, length = 50)
    private String tipo;

    // Classificação = idade mínima recomendada (a empresa atende crianças de até 15 anos)
    @NotNull(message = "A classificação (idade mínima recomendada) é obrigatória.")
    @Min(value = 0, message = "A classificação não pode ser negativa.")
    @Max(value = 15, message = "A classificação máxima é 15 anos.")
    @Column(name = "CLASSIFICACAO", nullable = false)
    private Integer classificacao;

    @NotBlank(message = "O tamanho é obrigatório (ex.: P, M, G, N.4).")
    @Size(max = 20, message = "O tamanho deve ter no máximo 20 caracteres.")
    @Column(name = "TAMANHO", nullable = false, length = 20)
    private String tamanho;

    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "O preço aceita até 8 dígitos inteiros e 2 decimais.")
    @Column(name = "PRECO", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    public Brinquedo() {
    }

    public Brinquedo(String nome, String tipo, Integer classificacao, String tamanho, BigDecimal preco) {
        this.nome = nome;
        this.tipo = tipo;
        this.classificacao = classificacao;
        this.tamanho = tamanho;
        this.preco = preco;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getClassificacao() { return classificacao; }
    public void setClassificacao(Integer classificacao) { this.classificacao = classificacao; }

    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
}
