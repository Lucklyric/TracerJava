%Function For Draw Distance Circle
function drawDistanceCircle(array1,array2,array3)
figure
subplot(2,2,1),drawFigure(array1),title('AB');
subplot(2,2,2),drawFigure(array2),title('AC');
subplot(2,2,3),drawFigure(array3),title('BC');
    function drawFigure(array)
        normalAngle = 360.0/(numel(array));
        plotX = zeros(1,numel(array));
        plotY = zeros(1,numel(array));
        for i=1:numel(array)
            plotX(i) = cos(i*normalAngle)*array(i);
            plotY(i) = sin(i*normalAngle)*array(i);
        end
        PlotAxisAtOrigin(plotX,plotY);
    end
end

