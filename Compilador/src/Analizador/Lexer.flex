package Analizador;
import java.io.*;
import Controlador.FuncionesCompilador;
import Analizador.TokenType;

%%

%class AnalizadorLexico
%unicode
%public
%line
%column
%type Token

%{
  private Token token(TokenType tipo, String valor) {
      return new Token(tipo, valor, yyline + 1, yycolumn + 1);
  }

  private Token errorToken(String valor) {
      FuncionesCompilador.agregarErrorLexico(valor, yyline + 1, yycolumn + 1);
      return new Token(TokenType.ERROR, valor, yyline + 1, yycolumn + 1);
  }
%}

// Macros
DIGITO      = [0-9]
LETRA       = [a-zA-Z_]
IDENT       = {LETRA}({LETRA}|{DIGITO})*
NUMERO      = {DIGITO}+ ( \. {DIGITO}+ )? 
CADENA      = \" ( [^\"\\\n\r] | \\. )* \"
ESPACIO     = [ \t\r\n\f]+
COMENTARIO_LINEA = "//" [^\r\n]* ( \r | \n | \r\n )?
COMENTARIO_BLOQUE = "/*" ( [^*] | \*+[^*/] )* \*+ "/"

%%

/* ------------------------------------------------------------------------
   1. PALABRAS RESERVADAS (Deben ir PRIMERO para ganar prioridad sobre ID)
   ------------------------------------------------------------------------ */
"programa"    { return token(TokenType.PROGRAMA, yytext()); }
"finprograma" { return token(TokenType.FIN_PROGRAMA, yytext()); }
"var"         { return token(TokenType.VAR, yytext()); }
"entero"      { return token(TokenType.ENTERO, yytext()); }
"real"        { return token(TokenType.REAL, yytext()); }
"cadena"      { return token(TokenType.CADENA, yytext()); }
"bool"        { return token(TokenType.BOOL, yytext()); }
"if"          { return token(TokenType.IF, yytext()); }
"else"        { return token(TokenType.ELSE, yytext()); }
"endif"       { return token(TokenType.ENDIF, yytext()); }
"leer"        { return token(TokenType.LEER, yytext()); }
"escribir"    { return token(TokenType.ESCRIBIR, yytext()); }
"true"        { return token(TokenType.TRUE, yytext()); }
"false"       { return token(TokenType.FALSE, yytext()); }

/* Compatibilidad con int/float si los usas */
"int"         { return token(TokenType.ENTERO, yytext()); }
"float"       { return token(TokenType.REAL, yytext()); }
"boolean"     { return token(TokenType.BOOL, yytext()); }
"string"      { return token(TokenType.CADENA, yytext()); }

/* ------------------------------------------------------------------------
   2. OPERADORES COMPUESTOS
   ------------------------------------------------------------------------ */
"++"          { return token(TokenType.INCREMENTO, yytext()); }
"--"          { return token(TokenType.DECREMENTO, yytext()); }
"=="          { return token(TokenType.IGUAL, yytext()); }
"!="          { return token(TokenType.DIFERENTE, yytext()); }
"<="          { return token(TokenType.MENOR_IGUAL, yytext()); }
">="          { return token(TokenType.MAYOR_IGUAL, yytext()); }
"&&"          { return token(TokenType.AND, yytext()); }
"||"          { return token(TokenType.OR, yytext()); }

/* ------------------------------------------------------------------------
   3. OPERADORES SIMPLES Y DELIMITADORES
   ------------------------------------------------------------------------ */
"+"           { return token(TokenType.SUMA, yytext()); }
"-"           { return token(TokenType.RESTA, yytext()); }
"*"           { return token(TokenType.MULTIPLICACION, yytext()); }
"/"           { return token(TokenType.DIVISION, yytext()); }
"^"           { return token(TokenType.POTENCIA, yytext()); }
"%"           { return token(TokenType.MODULO, yytext()); }
">"           { return token(TokenType.MAYOR, yytext()); }
"<"           { return token(TokenType.MENOR, yytext()); }
"!"           { return token(TokenType.NOT, yytext()); }
"="           { return token(TokenType.ASIGNACION, yytext()); }

";"           { return token(TokenType.PUNTOYCOMA, yytext()); }
","           { return token(TokenType.COMA, yytext()); }        /* <-- FALTABA ESTE */
":"           { return token(TokenType.DOS_PUNTOS, yytext()); }  /* <-- FALTABA ESTE */

"("           { return token(TokenType.PAREN_ABRE, yytext()); }
")"           { return token(TokenType.PAREN_CIERRA, yytext()); }
"{"           { return token(TokenType.LLAVE_ABRE, yytext()); }
"}"           { return token(TokenType.LLAVE_CIERRA, yytext()); }

/* ------------------------------------------------------------------------
   4. IDENTIFICADORES Y LITERALES (Deben ir AL FINAL)
   ------------------------------------------------------------------------ */
{IDENT}       { return token(TokenType.ID, yytext()); }
{NUMERO}      { return token(TokenType.NUM, yytext()); }
{CADENA}      { return token(TokenType.CADENA_LITERAL, yytext()); }

/* ------------------------------------------------------------------------
   5. IGNORADOS
   ------------------------------------------------------------------------ */
{ESPACIO}           { /* Ignorar */ }
{COMENTARIO_LINEA}  { /* Ignorar */ }
{COMENTARIO_BLOQUE} { /* Ignorar */ }

/* ------------------------------------------------------------------------
   6. ERROR
   ------------------------------------------------------------------------ */
.             { return errorToken(yytext()); }