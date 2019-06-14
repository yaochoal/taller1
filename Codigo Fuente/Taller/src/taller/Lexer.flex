package taller;
import static taller.Tokens.*;
%%
%class Lexer
%type Tokens
L=[a-zA-Z_]+
D=[0-9]+
C=<<{L}({L}|{D})*>>
espacio=[ ,\t,\r,\n]+
%{
    public String lexeme;
%}
%%
while {lexeme=yytext(); return Reservadas;}
{espacio} {/*Ignore*/}
{C} {lexeme=yytext(); return Campo;}
 . {return ERROR;}