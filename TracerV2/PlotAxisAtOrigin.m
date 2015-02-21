function PlotAxisAtOrigin(x,y)
%PlotAxisAtOrigin Plot 2D axes through the origin
%   This is a 2D version of Plot3AxisAtOrigin written by Michael Robbins
%   File exchange ID: 3245. 
%
%   Have hun! 
%
%   Example:
%   x = -2*pi:pi/10:2*pi;
%   y = sin(x);
%   PlotAxisAtOrigin(x,y)
%

% PLOT
if nargin == 2 
    plot(x,y,'.');
    hold on;
else
    display('   Not 2D Data set !')
end;

% GET TICKS
X=get(gca,'Xtick');
Y=get(gca,'Ytick');

% GET LABELS
XL=get(gca,'XtickLabel');
YL=get(gca,'YtickLabel');

% GET OFFSETS
Xoff=diff(get(gca,'XLim'))./40;
Yoff=diff(get(gca,'YLim'))./40;

% DRAW AXIS LINEs
plot([-max(x)-1,max(x)+1],[0 0],'k');
plot([0 0],[-max(y)-1,max(y)+1],'k');

% Plot new ticks  
for i=1:length(X)
    plot([X(i) X(i)],[0 Yoff],'-k');
end;
for i=1:length(Y)
   plot([Xoff, 0],[Y(i) Y(i)],'-k');
end;
% ADD LABELS
%text(X,zeros(size(X))-.2*Yoff,XL);
%text(zeros(size(Y))-.2*Xoff,Y,YL);

box on;
% axis square;
axis on;
axis([-max(x)-1,max(x)+1,-max(y)-1,max(y)+1]);
grid on;
set(gcf,'color','w');

