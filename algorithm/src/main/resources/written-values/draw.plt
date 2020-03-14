reset

set datafile separator whitespace

fileout = "_img.png"

maintitle = "Tutaj nasz tytuł lub jego brak"
titlenum = "opis osi lub brak"

set xlabel "{/:Italic x [jednostki]}"
set ylabel "{/:Italic y [jednostki]}"

lwidth = 3

set format x "%.0f"
set format y "%.0f"

colToPlot = 3

###

set term pngcairo size 1280,720 enhanced font "Times New Roman, 20"
set out fileout

set xrange [*:*]
set yrange [*:*]

set grid

set title maintitle

p filein using 1:colToPlot w l lw lwidth lc rgb 'red' t titlenum