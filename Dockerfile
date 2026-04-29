# Estágio 1: Build da Aplicação (usando o JDK)
# Usamos uma imagem JDK completa para compilar o código
FROM eclipse-temurin:21-jdk-jammy AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia o Maven Wrapper
COPY mvnw .
COPY .mvn ./.mvn

# Copia o arquivo de definição do build
COPY pom.xml .

# Dá permissão de execução ao wrapper
RUN chmod +x ./mvnw

# Baixa as dependências primeiro para aproveitar o cache de camadas do Docker
# O parâmetro -B (batch mode) deixa o log menos poluído
RUN ./mvnw dependency:go-offline -B

# Copia o restante do código-fonte
COPY src ./src

# Constrói a aplicação (gera o .jar) pulando os testes
RUN ./mvnw clean package -DskipTests

# Estágio 2: Imagem Final (Runtime com Alpine)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria um usuário não-root para rodar a aplicação
RUN adduser -D appuser

# Argumento para o caminho do JAR (no Maven, o build fica na pasta target)
ARG JAR_FILE_PATH=target/*.jar

# Copia o JAR construído do estágio de build para a imagem final
COPY --from=build /app/${JAR_FILE_PATH} app.jar

# Altera o proprietário e o grupo do arquivo app.jar do usuário root para o usuário appuser
RUN chown appuser:appuser app.jar

# Altera o usuário ativo para o usuário não-root
USER appuser

# Expõe a porta padrão definida
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-Xmx400m", "-jar", "app.jar"]