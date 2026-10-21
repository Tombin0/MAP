Esercizi svolti in pseudo-ADA

Fornire una realizzazione di una tipo astratto Dizionario in Ada (si ipotizzi che Chiave e Valore siano DI TIPO Intero e Carattere). Per tale esercizio considerare la specifica sintattica fornita nel seguito 

creaDizionario()-->Dizionario 
aggiungi(Dizionario,Chiave,Valore)-->Dizionario 
leggi(Dizionario,Chiave)-->Valore 
cancella(Dizionario,Chiave)-->Dizionario 
uguale(Dizionario,Dizionario)-->Booleano 

Mostrare l'uso del tipo scritto. Commentare il codice scritto.

package TypeDizionario
begin
type Dizionario is limited private;
procedure aggiungi(d: in out Dizionario, k: in Integer, v: in Character);
function leggi(d:in Dizionario, k: in Integer) return Character;
procedure cancella(d: in out Dizionario, k: in Integer);
function uguale(d1: in Dizionario, d2: in Dizionario) return Boolean;
private
const max=100;
type Dizionario= record
chiavi: array(1..,max) of Integer;
valori: array(1,..,max) of Character;
top: Integer range 0,..,max=0;
end record
end package

package body TypeDizionario
begin
procedure aggiungi(d: in out Dizionario, k: in Integer, v: in Character)
begin
	if d.top=max then
		raise “Eccezione”;
	else
	begin
		for i:=1 to d.top
			if d.chaivi(i)=k then return;
		d.top:=d.top+1;
		d.chiavi(d.top):=k;
		d.valori(d.top):=v;
	end
end

function leggi(d:in Dizionario, k: in Integer) return Character
var i:integer;
begin
	for i:=1 to d.top 
		if k=d.chiavi(i) then return d.valori(i)
	raise “Eccezione”;
end

procedure cancella(d: in out Dizionario, k: in Integer)
var i:integer
begin
	for i:=1 to d.top
		if d.chiavi(i)= k then
		begin
			d.chiavi(i):=d.chiavi(d.top);
			d.valori(i):=d.valori(d.top);
			d.top:= d.top-1;
return;
		end
end
function uguale(d1: in Dizionario, d2: in Dizionario) return Boolean
var i:integer;
begin
	if d1.top<>d2.top then return false;
	else
		for i:=1 to d1.top 
			if appartiene(d2,d1.chiavi(i), d1.valori(i)) = false then return false
		reurn true;
end

function appartiene(d:in Dizionario, k: in Integer, v: in Character) return Boolean
var i: integer;
begin
	for i:=1 to d.top
		if d.chiavi(i)=k && d.valori(i)= v then return true;
	return false;
end

end TypeDizionario


…


with TypeDizionario; use TypeDizionario;

var Diz1, Diz2: Dizionario;

aggiungi(Diz1,1,’m’);
aggiungi(Diz1,1,’s’);
aggiungi(Diz1,2,’s’);
aggiungi(Diz2,1,’m’);

aggiungi(Diz2,2,’s’);
cancella(Diz2,2);
if uguale(Diz1,Diz2) then put “sono uguali”;
else put “sono diversi”


Fornire una realizzazione di una classe Dizionario in Ada (si ipotizzi che Chiave e Valore siano DI TIPO Intero e Carattere). Per tale esercizio considerare la specifica sintattica fornita nel seguito 
creaDizionario()Dizionario 
aggiungi(Dizionario,Chiave,Valore)Dizionario 
leggi(Dizionario,Chiave)Valore 
cancella(Dizionario,Chiave)Dizionario 
uguale(Dizionario,Dizionario)Booleano 
Mostrare l'uso della classe scritta. Commentare il codice scritto.



Fornire una realizzazione di una tipo astratto Dizionario in Ada (si ipotizzi che Chiave e Valore siano due parametri generici). Per tale esercizio considerare la specifica sintattica fornita nel seguito 
creaDizionario()Dizionario 
aggiungi(Dizionario,Chiave,Valore)Dizionario 
leggi(Dizionario,Chaive)Valore 
cancella(Dizionario,Chiave)Dizionario 
uguale(Dizionario,Dizionario)Booleano 
Mostrare l'uso del tipo scritto. Commentare il codice scritto.

Fornire una realizzazione di una classe Dizionario in Ada (si ipotizzi che Chiave e Valore siano due parametri generici). Per tale esercizio considerare la specifica sintattica fornita nel seguito 
creaDizionario()Dizionario 
aggiungi(Dizionario,Chiave,Valore)Dizionario 
leggi(Dizionario,Chaive)Valore 
cancella(Dizionario,Chiave)Dizionario 
uguale(Dizionario,Dizionario)Booleano 
Mostrare l'uso della classe scritta. Commentare il codice scritto.

CLASSI

generic

package dizionarioclasse

begin
procedure aggiungidizionario(k:in Integer, v:in Character);

function leggi(k:in integer) return Character;

procedure cancella(k:in integer);
end dizionarioclasse

package body dizionarioclasse
begin
const max:=100
var chiavi: array (1..max) of Integer;
Valori: array(1..max) of Character
top: integer range(0..max):=0;


procedure aggiungidizionario(k:in Integer, v:in Character)
var i: integer
begin
	if top=max then raise "Eccezione dio merda"
	else
	begin
	for i:=1 to top
		if chiavi(i)==k then return;
		
		top:=top+1;
		chiavi(top):=k;
		valori(top):=v;
	end
end

function leggi(k:in integer) return Character
var i: integer
begin
	for i:=1 to top then
		if chiavi(i)==k then return Valori (i);
	raise "Eccezione dio merda"
end


procedure cancella(k:in integer)
var i: Integer
begin
	for i:=1 to top
		if chiavi(i)=k then
		begin
			chiavi(i):=chiavi(top);
			valori(i):=valori(top);
			top:=top-1;
			return;
		end
	end 
end

end dizionarioclasse

...

with dizionarioclasse;
package diz1 is new dizionarioclasse;
with diz1; use diz1;
package diz2 is new dizionarioclasse;
with diz2; use diz2;

diz1.aggiungi(1', m')
diz2.aggiungi(2', s')
diz1.cancella(1)
diz2.aggiungi(3', h')