%%% Util fuctions for setting tabs
% - Sections for table of contents http://lsr.dsi.unimi.it/LSR/Item?id=738
% - Triplet feel from http://lsr.dsi.unimi.it/LSR/Item?id=204
% - Deadnote and Palmmute tweaks from tuxguitar

% taken from LSR 368 (http://lsr.dsi.unimi.it/LSR/Item?id=368)
#(define-markup-command (vspace layout props amount) (number?)
  "This produces a invisible object taking vertical space."
  (let ((amount (* amount 3.0)))
       (if (> amount 0)
           (ly:make-stencil "" (cons -1 1) (cons 0 amount))
           (ly:make-stencil "" (cons -1 1) (cons amount amount)))))

% define toc-section commands
#(begin 
  (define-public (set-toc-section! text) #f)
  (define-public (get-toc-section text) #f)
  (let ((toc-section #f))
       (set! set-toc-section! (lambda (text)(set! toc-section text)))
       (set! get-toc-section (lambda ()(begin toc-section)))
  )
)

% define section-aware piece-toc-item-command
piece = #(define-music-function (parser location text) (markup?)
  (begin 
    (if (get-toc-section) (add-toc-item! 'tocCollMarkup (get-toc-section)))
    (set-toc-section! #f)
    (add-toc-item! 'tocPartMarkup text))
)

deadNote = #(define-music-function (parser location note) (ly:music?)
   (set! (ly:music-property note 'tweaks)
      (acons 'stencil ly:note-head::print
         (acons 'glyph-name "2cross"
            (acons 'style 'special
               (ly:music-property note 'tweaks)))))
   note)

palmMute = #(define-music-function (parser location note) (ly:music?)
   (set! (ly:music-property note 'tweaks)
      (acons 'style 'do (ly:music-property note 'tweaks)))
   note)

%%% Function: rhythmMark
%%% ============================================================
%%%  Purpose: print a sophisticated rehearsal mark e.g
%%%           for rhythm directives
%%%    Usage: \rhythmMark label music1 music2
%%% ------------------------------------------------------------
%%% Variable: label (string)
%%% ------------------------------------------------------------
%%% Variable: music1 (ly:music)
%%% ------------------------------------------------------------
%%% Variable: music2 (ly:music)
%%% ------------------------------------------------------------
%%%  Example: \rhythmMark #"Swing" \rhyMarkIIEighths
%%%                 \rhyMarkSlurredTriplets
%%% ------------------------------------------------------------
%%% Constants:
%%%           rhythmMarkStaffReduce = #-3
%%%           rhythmMarkLabelFontSize = #-2
%%% ------------------------------------------------------------
%%%  Comment: see below for predefined values for music1&2
%%% ============================================================

rhythmMarkStaffReduce = #-3
rhythmMarkLabelFontSize = #-2

rhythmMark = #(define-music-function (parser location label musicI musicII ) (string? ly:music? ly:music?)
   #{
      \mark \markup {
        \line \general-align #Y #DOWN {
          \combine
            \italic \fontsize #rhythmMarkLabelFontSize $label
            \transparent \italic \fontsize #rhythmMarkLabelFontSize f

          \score {                     % 2nd column in line
            \new Staff \with {
              fontSize = #rhythmMarkStaffReduce
              \override StaffSymbol #'staff-space = #(magstep rhythmMarkStaffReduce)
              \override StaffSymbol #'line-count = #0
              \override VerticalAxisGroup #'Y-extent = #'(-0.85 . 4)
            }

            {
              \relative { \stemUp $musicI }
             
%             \override Score.SpacingSpanner #'strict-note-spacing = ##t
              \once \override Score.TextScript #'Y-offset = #-0.4
              s4.^\markup{ \halign #-1 \italic "=" }
              
              \relative { \stemUp $musicII }
            } 
            
            \layout {
              ragged-right= ##t
              indent = 0
              \context {
                \Staff
                \remove "Clef_engraver"
                \remove "Time_signature_engraver"
              }
            } % layout end

          } % Score end

        } % line end
      } % markup end
   #})

%%% Function: rhythmMarkC
%%% ============================================================
%%%  Purpose: print a sophisticated rehearsal mark e.g for
%%%           rhythm directives in a column (music on top)
%%%    Usage: \rhythmMarkC label music1 music2
%%% ------------------------------------------------------------
%%% Variable: label (string)
%%% ------------------------------------------------------------
%%% Variable: music1 (ly:music)
%%% ------------------------------------------------------------
%%% Variable: music2 (ly:music)
%%% ------------------------------------------------------------
%%%  Example: \rhythmMarkC #"Swing" \rhyMarkIIEighths
%%%                 \rhyMarkSlurredTriplets
%%% ------------------------------------------------------------
%%% Constants:
%%%           rhythmMarkCStaffReduce = #-4
%%%           rhythmMarkCLabelFontSize = #-2
%%% ------------------------------------------------------------
%%%  Comment: see below for predefined values for music1&2
%%% ============================================================

rhythmMarkCStaffReduce = #-4
rhythmMarkCLabelFontSize = #-2

rhythmMarkC = #(define-music-function (parser location label musicI musicII ) (string? ly:music? ly:music?)
   #{
      \mark \markup
      {
        \combine

          \line {
            \hspace #0
            \translate #'(-0.1 . -3.25) \italic \fontsize #rhythmMarkCLabelFontSize $label
          } % end Line

          \line \vcenter {

              \score {                 % 1st column in line

                \new Staff \with {
                  fontSize = #rhythmMarkCStaffReduce
                  \override StaffSymbol #'staff-space = #(magstep rhythmMarkCStaffReduce)
                  \override StaffSymbol #'line-count = #0 
                  \override VerticalAxisGroup #'Y-extent = #'(0 . 0)  % td
                }

                \relative { \stemUp $musicI }

                \layout {
                  ragged-right= ##t
                  indent = 0
                  \context {
                    \Staff
                    \remove "Clef_engraver"
                    \remove "Time_signature_engraver" }
                } % layout

              } % 1st score

              \hspace #-0.1            % 2nd column in line

                                       % 3rd column in line
              \italic \fontsize #rhythmMarkCStaffReduce "="

              \score {                 % 4th column in line

                \new Staff \with {
                  fontSize = #rhythmMarkCStaffReduce
                  \override StaffSymbol #'staff-space = #(magstep rhythmMarkCStaffReduce)
                  \override StaffSymbol #'line-count = #0 
                  \override VerticalAxisGroup #'Y-extent = #'(0 . 0)  % td
                }

                \relative {
                  \stemUp $musicII
                }

                \layout {
                  ragged-right= ##t
                  indent = 0
                  \context {
                    \Staff
                    \remove "Clef_engraver"
                    \remove "Time_signature_engraver" }
                } % layout

              } % 2nd score end

            } % line end
          % end combine
        } % markup end
   #})

%%% predefined ly:music-Variables for use
%%% in function rhythmMark and rhythmMarkC
%%% ============================================================
rhyMarkI = { b'1*1/8 }
rhyMarkII = { b'2*1/4 }
rhyMarkIV = { b'4*1/2 }
rhyMarkEighth = { b'8 }

rhyMarkIIEighths = {
  % \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 1 4) % tight
  \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 3 16) % even
  b'8[ b8]
}
rhyMarkTriplets = {
  % \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 1 2) % super-tight
  % \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 1 4) % tight
  \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 3 16) % even
  \times 2/3 { b'4 b8 }
}
rhyMarkSlurredTriplets = {
  % \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 1 4) % tight
  % \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 5 32) % slighty tighter as even
  \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 1 8) % even
  \times 2/3 { b'8 ~ b8 b8 }
}
rhyMarkDottedEighths = {
  % \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 1 4) % tight
  \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 3 16) % even
  % \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 1 8) % loose
  { b'8.[ b16*2] }
}

