DEPS:=$(shell ls *.tex)
main.pdf:$(DEPS)
	pdflatex main.tex
	pdflatex main.tex
	pdflatex main.tex
.PHONY:clean_all, clean
cleanall:
	rm -f *aux *log *toc *bbl *blg *pdf *out
clean:
	rm -f *aux *log *toc *bbl *blg *out
