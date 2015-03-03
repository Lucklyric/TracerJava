%Function For Draw Distance Circle
function drawDistanceCircle(array1,array2,array3,array4,array5,array6)
figure
subplot(3,2,1),drawFigure(array4),title('BA');
subplot(3,2,2),drawFigure(array5),title('CA');
subplot(3,2,3),drawFigure(array1),title('AB');
subplot(3,2,4),drawFigure(array6),title('CB');
subplot(3,2,5),drawFigure(array2),title('AC');
subplot(3,2,6),drawFigure(array3),title('BC');
    function drawFigure(array)
        normalAngle = 360.0/(numel(array));
        plotX = zeros(1,numel(array));
        plotY = zeros(1,numel(array));
        for i=1:numel(array)
            plotX(i) = (cosd(i*normalAngle))*array(i);
            plotY(i) = (sind(i*normalAngle))*array(i);
        end
        PlotAxisAtOrigin(plotX,plotY);
    end
end

