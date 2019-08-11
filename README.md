# textools [![Build Status](https://travis-ci.org/simonharrer/textools.png?branch=master)](https://travis-ci.org/simonharrer/textools) 

UNMAINTAINED!

Textools provides CLI commands for the most commonly used tasks when working with [LaTeX](http://www.latex-project.org/),
e.g., generating a `.gitignore` file, creating the final pdf and validating the `.tex` and `.bib` files.

**Only** works for UTF-8 encoded `.tex` and `.bib` files.

## Installation

Requires JDK 8 with JAVA_HOME set to the JDK path!

    $ git clone https://github.com/simonharrer/textools.git
    $ cd textools
    $ ./gradlew installDist
    # add textools/build/install/textools/bin to PATH

## Usage

    # in your latex directory
    $ textools pdf # create the pdf with pdflatex and bibtex using main.tex as the starting file
    $ textools validate # validates all .tex and .bib files using Simon's validation rules
    $ textools clean # remove all generated files like .div, .pdf, .log, ...

## Commands

	textools [command]
	
	 cites                        Print used cites
	 clean                        Removes all generated files during a tex build
	 create-gitignore             creates a latex project specific .gitignore file
	 help                         prints usage information
	 minify-bibtex-authors        replace three or more authors with et al. in bibtex entries
	 minify-bibtex-optionals      removes optional keys in bibtex entries
	 pdf                          creates pdf with pdflatex, including bibtex; logs to textools-pdf.log
	 pdfclean                     executes pdf and clean commands in sequence
	 texlipse                     generates texlipse project files
	 texniccenter                 generates the texniccenter project files
	 validate                     executes validate-latex and validate-bibtex commands in sequence
	 validate-acronym             detects unmarked acronyms in text
	 validate-bibtex              validates all .bib files for the existence of certain fields
	 validate-labels              detects unused labels
	 validate-latex               validates .tex files
	 validate-links               detects malformed and unreachable urls
	 version                      prints the current version

## Works best when

- the citation style is numeric/alphanumeric.
- each sentence is in its own line.
- labels in tables/figures should be put right after the caption
- all files are in UTF-8

## Authors

[Simon Harrer](mailto:simon.harrer@gmail.com)

## Contribute

    10 Fork
    20 Create feature branch
    30 Create commits
    40 Create pull request
    GOTO 10
