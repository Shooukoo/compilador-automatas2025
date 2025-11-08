package Analizador;

import java.io.*;
// Importar FuncionesCompilador para reportar errores
import Controlador.FuncionesCompilador;
// Importar TokenType para usar el tipo ERROR
import Analizador.TokenType;

%%

// --- Directivas JFlex ---
%class AnalizadorLexico      // Nombre de la clase Java a generar
%unicode                    // Soporte para caracteres Unicode
%public                     // Hacer la clase pública
%line                       // Habilitar contador de líneas (yyline, empieza en 0)
%column                     // Habilitar contador de columnas (yycolumn, empieza en 0)
%type Token                 // Tipo de objeto que devolverá yylex()

// --- Código Java Embebido ---
%{
  /**
   * Método de conveniencia para crear un nuevo Token VÁLIDO.
   * Ajusta la línea y columna para que empiecen en 1.
   */
  private Token token(TokenType tipo, String valor) {
      // Sumamos 1 a yyline y yycolumn porque JFlex los cuenta desde 0
      return new Token(tipo, valor, yyline + 1, yycolumn + 1);
  }

  /**
   * Reporta un error léxico Y DEVUELVE un token de tipo ERROR.
   * @param valor El lexema no reconocido que causó el error.
   * @return Un Token de tipo TokenType.ERROR.
   */
  private Token errorToken(String valor) {
      // 1. Reportar el error a la lista central
      FuncionesCompilador.agregarErrorLexico(valor, yyline + 1, yycolumn + 1);
      
      // 2. Devolver un token especial de ERROR
      // Esto permite al parser decidir si ignora este token o intenta alguna recuperación.
      return new Token(TokenType.ERROR, valor, yyline + 1, yycolumn + 1);
  }
%}

// --- Definiciones de Macros (Expresiones Regulares Nombradas) ---
DIGITO      = [0-9]
LETRA       = [a-zA-Z_]
IDENT       = {LETRA}({LETRA}|{DIGITO})*
NUMERO      = {DIGITO}+ ( \. {DIGITO}* )?
CADENA      = \" ( [^\"\\\n\r] | \\. )* \"
ESPACIO     = [ \t\r\n\f]+

// Comentarios
COMENTARIO_LINEA = "//" [^\r\n]* ( \r | \n | \r\n )?
COMENTARIO_BLOQUE = "/*" ( [^*] | \*+[^*/] )* \*+ "/"

%%

// --- Reglas Léxicas y Acciones ---

// Palabras reservadas (importante ponerlas antes de IDENT)
"int"                   { return token(TokenType.INT, yytext()); }
"float"                 { return token(TokenType.FLOAT, yytext()); }
"boolean"               { return token(TokenType.BOOLEAN, yytext()); }
"string"                { return token(TokenType.STRING, yytext()); }
"true"                  { return token(TokenType.TRUE, yytext()); }
"false"                 { return token(TokenType.FALSE, yytext()); }

// Operadores aritméticos
"+"                     { return token(TokenType.SUMA, yytext()); }
"-"                     { return token(TokenType.RESTA, yytext()); }
"*"                     { return token(TokenType.MULTIPLICACION, yytext()); }
"/"                     { return token(TokenType.DIVISION, yytext()); }
"^"                     { return token(TokenType.POTENCIA, yytext()); }
"%"                     { return token(TokenType.MODULO, yytext()); }

// Operadores relacionales
"=="                    { return token(TokenType.IGUAL, yytext()); }
"!="                    { return token(TokenType.DIFERENTE, yytext()); }
"<="                    { return token(TokenType.MENOR_IGUAL, yytext()); }
">="                    { return token(TokenType.MAYOR_IGUAL, yytext()); }
">"                     { return token(TokenType.MAYOR, yytext()); }
"<"                     { return token(TokenType.MENOR, yytext()); }

// Operadores lógicos
"&&"                    { return token(TokenType.AND, yytext()); }
"||"                    { return token(TokenType.OR, yytext()); }
"!"                     { return token(TokenType.NOT, yytext()); }

// Otros símbolos y delimitadores
"="                     { return token(TokenType.ASIGNACION, yytext()); }
";"                     { return token(TokenType.PUNTOYCOMA, yytext()); }
"("                     { return token(TokenType.PAREN_ABRE, yytext()); }
")"                     { return token(TokenType.PAREN_CIERRA, yytext()); }
"{"                     { return token(TokenType.LLAVE_ABRE, yytext()); }
"}"                     { return token(TokenType.LLAVE_CIERRA, yytext()); }

// Identificadores y literales (después de palabras clave)
{IDENT}                 { return token(TokenType.IDENTIFICADOR, yytext()); }
{NUMERO}                { return token(TokenType.NUMERO, yytext()); }
{CADENA}                { return token(TokenType.CADENA, yytext()); }

// Ignorar espacios y comentarios
{ESPACIO}               { /* No hacer nada, JFlex sigue buscando */ }
{COMENTARIO_LINEA}      { /* Ignorar */ }
{COMENTARIO_BLOQUE}     { /* Ignorar */ }

// --- Manejo de Error Léxico ---
// Cualquier otro carácter que no coincida con las reglas anteriores
.                       { return errorToken(yytext()); /* Reporta Y DEVUELVE un token ERROR */ }