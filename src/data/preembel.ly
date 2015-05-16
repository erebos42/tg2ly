%%% Util fuctions for setting tabs
% - Triplet feel based on http://lsr.di.unimi.it/LSR/Item?id=204
% - Deadnote and Palmmute tweaks from tuxguitar

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

%%% Function: rhythmMarkW
%%% ============================================================
%%%  Purpose: print a sophisticated rehearsal mark e.g
%%%           for rhythm directives without a label
%%%    Usage: \rhythmMark music1 music2
%%% ------------------------------------------------------------
%%% Variable: music1 (ly:music)
%%% ------------------------------------------------------------
%%% Variable: music2 (ly:music)
%%% ------------------------------------------------------------
%%%  Example: \rhythmMarkW \rhyMarkIIEighths
%%%                 \rhyMarkSlurredTriplets
%%% ------------------------------------------------------------
%%% Constants:
%%%           rhythmMarkStaffReduce = #-3
%%% ------------------------------------------------------------
%%%  Comment: see below for predefined values for music1&2
%%% ============================================================

rhythmMarkStaffReduce = #-3

rhythmMarkW = #(define-music-function (parser location musicI musicII ) (ly:music? ly:music?)
   #{
      \mark \markup {
     
        \hspace #8
     
        \score {
          \new Staff \with {
            fontSize = #rhythmMarkStaffReduce
            \override StaffSymbol #'staff-space = #(magstep rhythmMarkStaffReduce)
            \override StaffSymbol #'line-count = #0
          }

          {
            \relative { \stemUp $musicI }
            
            \once \override Rest.stencil = #ly:text-interface::print
            \once \override Rest.text = \markup {"="}
            r8
            
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
      } % markup end
   #})

%%% predefined ly:music-Variables for use
%%% in function rhythmMarkW
%%% ============================================================

rhyMarkIIEighths = {
  \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 3 16)
  b'8[ b8]
}

rhyMarkIISixteenth = {
  \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 3 16)
  b'16[ b16]
}

rhyMarkTriplets = {
  \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 3 16)
  \autoBeamOff
  \tuplet 3/2 { b'4 b8 }
}

rhyMarkSixteenthTriplets = {
  \override Score.SpacingSpanner #'common-shortest-duration = #(ly:make-moment 3 16)
  \autoBeamOff
  \tuplet 3/2 { b'8 b16 }
}
