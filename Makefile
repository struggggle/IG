PAPER=paper.tex

PDF=${PAPER:%.tex=%.pdf}

all:  clean ${PDF}

%.pdf:  %.dtx
	pdflatex $<
	- bibtex $*
	pdflatex $<
	- makeindex -s gind.ist -o $*.ind $*.idx
	- makeindex -s gglo.ist -o $*.gls $*.glo
	pdflatex $<
	while ( grep -q '^LaTeX Warning: Label(s) may have changed' $*.log) \
	do pdflatex $<; done

%.cls:  %.ins %.dtx
	pdflatex $<

%.pdf:  %.tex
	pdflatex $<
	- bibtex $*
	pdflatex $<
	pdflatex $<
	while ( grep -q '^LaTeX Warning: Label(s) may have changed' $*.log) \
	do pdflatex $<; done

paper.pdf: *.tex

clean:
	$(RM) paper.pdf *.log *.aux *.synctex.gz \
	*.cfg *.glo *.idx *.toc \
	*.ilg *.ind *.out *.lof \
	*.lot *.bbl *.blg *.gls *.cut *.hd \
	*.dvi *.ps *.thm *.tgz *.zip *.rpi *.cpt *.DS_Store

incr:
	pdflatex $(PAPER)

html:
	pandoc $(PAPER) -t html > paper.html

NB := figures/newsblur-iid
NB_CODE := figures/newsblur-code

figures:
	pdfcrop $(NB).pdf && mv $(NB)-crop.pdf $(NB).pdf
	pdfcrop $(NB_CODE).pdf && mv $(NB_CODE)-crop.pdf $(NB_CODE).pdf

.PHONY: figures
