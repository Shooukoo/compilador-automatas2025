package Analizador;

import java.io.*;

%%

%class AnalizadorLexico
%unicode
%public
%line
%column
%type Token

%{
  // Código embebido dentro del lexer
  private Token token(TokenType tipo, String valor) {
      return new Token(tipo, valor);
  }
%}

/* Expresiones regulares */
DIGITO      = [0-9]
LETRA       = [a-zA-Z_]
IDENT       = {LETRA}({LETRA}|{DIGITO})*
NUMERO      = {DIGITO}+(\.{DIGITO}+)?   // números enteros y decimales
CADENA      = \"([^\"\\]|\\.)*\"        // cadena entre comillas
ESPACIO     = [ \t\r\n]+

%%

/* Palabras reservadas, tipos de datos */
"int"                   { return token(TokenType.INT, yytext()); }
"float"                 { return token(TokenType.FLOAT, yytext()); }
"boolean"               { return token(TokenType.BOOLEAN, yytext()); }
"string"                { return token(TokenType.STRING, yytext()); }
"true"                  { return token(TokenType.TRUE, yytext()); }
"false"                 { return token(TokenType.FALSE, yytext()); }

/* Operadores aritméticos */
"+"                     { return token(TokenType.SUMA, yytext()); }
"-"                     { return token(TokenType.RESTA, yytext()); }
"*"                     { return token(TokenType.MULTIPLICACION, yytext()); }
"/"                     { return token(TokenType.DIVISION, yytext()); }
"^"                     { return token(TokenType.POTENCIA, yytext()); }
"%"                     { return token(TokenType.MODULO, yytext()); }

/* Operadores relacionales */
"=="                    { return token(TokenType.IGUAL, yytext()); }
"!="                    { return token(TokenType.DIFERENTE, yytext()); }
"<="                    { return token(TokenType.MENOR_IGUAL, yytext()); }
">="                    { return token(TokenType.MAYOR_IGUAL, yytext()); }
">"                     { return token(TokenType.MAYOR, yytext()); }
"<"                     { return token(TokenType.MENOR, yytext()); }

/* Operadores lógicos */
"&&"                    { return token(TokenType.AND, yytext()); }
"||"                    { return token(TokenType.OR, yytext()); }
"!"                     { return token(TokenType.NOT, yytext()); }

/* Otros símbolos */
"="                     { return token(TokenType.ASIGNACION, yytext()); }
";"                     { return token(TokenType.PUNTOYCOMA, yytext()); }
"("                     { return token(TokenType.PAREN_ABRE, yytext()); }
")"                     { return token(TokenType.PAREN_CIERRA, yytext()); }
"{"                     { return token(TokenType.LLAVE_ABRE, yytext()); }
"}"                     { return token(TokenType.LLAVE_CIERRA, yytext()); }

/* Identificadores y valores */
{IDENT}                 { return token(TokenType.IDENTIFICADOR, yytext()); }
{NUMERO}                { return token(TokenType.NUMERO, yytext()); }
{CADENA}                { return token(TokenType.CADENA, yytext()); }

/* Espacios y comentarios */
{ESPACIO}               { /* ignorar espacios */ }
"//".*                  { /* ignorar comentario de línea */ }

"/*"([^*]|\*+[^*/])*\*+ "/"  { /* ignorar comentario multilinea */ }

/* --- Error --- */
.                       { throw new RuntimeException("Símbolo no reconocido: " + yytext()); }
