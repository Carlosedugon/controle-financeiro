# Minhas Finanças — controle financeiro para Android

App de gestão financeira pessoal, offline, com visual de banco digital. A interface fica em `app/src/main/assets/index.html` (HTML + JavaScript puro) e roda dentro de um WebView Android. Os dados ficam salvos só no celular.

## O que tem

- **Início (dashboard):** saldo disponível, receitas e despesas do mês, saldo restante, renda comprometida, dívidas em aberto, empréstimos ativos e reserva. Gráficos de receitas x despesas e de evolução do patrimônio (toque numa barra ou ponto para ver os valores, ou use "Tabela"), além dos gastos por categoria. Toque numa categoria para abrir as subcategorias. Filtro por mês/ano no topo.
- **Lançamentos:** receitas e despesas com categoria, subcategoria, vencimento, forma de pagamento, status (pago / pendente / atrasado), recorrência (única, semanal, mensal, anual) e observações. Ao marcar uma conta recorrente como paga, a próxima é criada automaticamente.
- **Carro:** categoria própria com 14 subcategorias (combustível, manutenção/revisão, troca de óleo, pneus, seguro, IPVA, licenciamento, multas, estacionamento, pedágio, lavagem, peças, parcela do financiamento, documentação). Os lançamentos de carro aceitam km do odômetro e litros. A tela **Meu carro** mostra o total por subcategoria, os km rodados, o custo por km, o consumo (km/L), o preço médio do litro e as contas do carro a pagar. Dá para cadastrar mais de um veículo.
- **Cartões:** vários cartões, com limite total e disponível, dia de fechamento e vencimento, compras à vista e parceladas, faturas abertas, fechadas e pagas, e histórico de pagamentos. As compras entram como despesa no mês da fatura. Pagar a fatura só baixa o saldo, sem contar a despesa de novo.
- **Agenda:** calendário do mês com marcadores, contas atrasadas, próximos 7 dias, contas a pagar, receitas do mês, contas pagas e recorrentes. Alertas no Início para atrasos e vencimentos em até 3 dias.
- **Reserva de emergência:** meta igual ao custo de vida × 3, 6 ou 12 meses (ou um valor fixo), depósitos, retiradas, % atingido, quanto falta e histórico. Fica separada do saldo do dia a dia.
- **Empréstimos:** os que você pegou (credor, valor original, total com juros, taxa, parcelas pagas e restantes, saldo devedor, pagamentos parciais) e os que você concedeu (pessoa, valor, devolução prevista, recebimentos e status).
- **Objetivos:** veículo, casa, viagem, quitar dívidas etc., com valor desejado, prazo, aportes, progresso e quanto guardar por mês.
- **Categorias:** criar, editar e excluir categorias e subcategorias. Qualquer categoria pode ser marcada como "veículo" (por exemplo, uma moto).
- **Configurações:** saldo inicial, veículos, tema claro/escuro, botão de ocultar valores (olho no topo), exportar e importar backup (.json) e apagar tudo.

## Como gerar o APK (GitHub Actions)

1. Crie um repositório novo no GitHub (ex.: `controle-financeiro`).
2. Envie esta pasta para ele:
   ```bash
   cd controle-financeiro
   git init
   git add .
   git commit -m "Primeira versão"
   git branch -M main
   git remote add origin https://github.com/Carlosedugon/controle-financeiro.git
   git push -u origin main
   ```
3. No GitHub, abra a aba **Actions** → workflow **Gerar APK**. Ele roda sozinho a cada push (ou clique em *Run workflow*).
4. Quando terminar (cerca de 3 a 5 minutos), abra a execução e baixe o artefato **MinhasFinancas-apk**. É um .zip com o `MinhasFinancas.apk` dentro.
5. Passe o APK para o celular e instale. Na primeira vez, o Android vai pedir para permitir a instalação de fontes desconhecidas.

O APK é a versão *debug*: assinado com a chave de desenvolvimento e bom para uso próprio. Para publicar na Play Store, seria preciso configurar uma chave de assinatura de release.

## Testar sem instalar

Abra `app/src/main/assets/index.html` no navegador do computador. É o mesmo app.

## Backup

Os dados ficam no armazenamento interno do app. **Desinstalar o app apaga os dados**, então use *Mais → Configurações → Exportar backup* de vez em quando, principalmente antes de trocar de celular. Para restaurar, use *Importar backup*.

Ao atualizar o app (instalar um APK novo por cima), os dados são mantidos.

## Estrutura

```
app/src/main/assets/index.html        ← todo o app (telas, regras e cálculos)
app/src/main/java/.../MainActivity.java ← WebView + botão voltar + salvar/abrir backup
.github/workflows/build-apk.yml       ← compila o APK no GitHub
```
